package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ActivateAbilityAction;
import de.pinneddown.server.actions.PlayEffectAction;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.*;
import de.pinneddown.server.util.PlayerUtils;
import de.pinneddown.server.util.ThreatUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Component
public class PlayEffectSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;
    private PlayerUtils playerUtils;
    private ThreatUtils threatUtils;

    private HashMap<String, Long> playerEntities;

    public PlayEffectSystem(EventManager eventManager, EntityManager entityManager, BlueprintManager blueprintManager,
                            PlayerUtils playerUtils, ThreatUtils threatUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;
        this.playerUtils = playerUtils;
        this.threatUtils = threatUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::onPlayerEntityCreated);
        this.eventManager.addEventHandler(ActionType.PLAY_EFFECT, this::onPlayEffect);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        this.playerEntities = new HashMap<>();
    }

    private void onPlayerEntityCreated(GameEvent gameEvent) {
        PlayerEntityCreatedEvent eventData = (PlayerEntityCreatedEvent)gameEvent.getEventData();
        playerEntities.put(eventData.getPlayerId(), eventData.getEntityId());
    }

    private void onPlayEffect(GameEvent gameEvent) {
        PlayEffectAction eventData = (PlayEffectAction)gameEvent.getEventData();

        // Get player hand.
        long playerEntityId = playerEntities.getOrDefault(eventData.getPlayerId(), -1L);

        if (playerEntityId == -1L) {
            return;
        }

        // Remove card.
        if (!playerUtils.removeHandCard(playerEntityId, eventData.getBlueprintId())) {
            return;
        }

        // Play card.
        long entityId = blueprintManager.createEntity(eventData.getBlueprintId());

        // Increase threat.
        ThreatComponent cardThreatComponent = entityManager.getComponent(entityId, ThreatComponent.class);

        if (cardThreatComponent != null) {
            int newThreat = threatUtils.getThreat() + cardThreatComponent.getThreat();
            threatUtils.setThreat(newThreat);
        }

        // Instantiate abilities.
        AbilitiesComponent abilitiesComponent = entityManager.getComponent(entityId, AbilitiesComponent.class);

        if (abilitiesComponent != null) {
            ArrayList<Long> abilities = abilitiesComponent.getOrCreateAbilityEntities(blueprintManager);

            // Find matching ability.
            Optional<Integer> matchingAbility = findMatchingAbility(abilities, eventData.getTargetEntityId());

            if (matchingAbility.isPresent()) {
                ActivateAbilityAction activateAbilityAction =
                        new ActivateAbilityAction(entityId, matchingAbility.get(), eventData.getTargetEntityId());
                eventManager.queueEvent(ActionType.ACTIVATE_ABILITY, activateAbilityAction);
            }
        }

        // Notify listeners.
        CardPlayedEvent cardPlayedEventData = new CardPlayedEvent(entityId, eventData.getBlueprintId(), 0L);
        eventManager.queueEvent(EventType.CARD_PLAYED, cardPlayedEventData);

        // Remove abilities and card again.
        entityManager.removeEntity(entityId);
        eventManager.queueEvent(EventType.CARD_REMOVED, new CardRemovedEvent(entityId));

        playerUtils.addCardToDiscardPile(playerEntityId, eventData.getBlueprintId());
    }

    private Optional<Integer> findMatchingAbility(ArrayList<Long> abilities, long targetEntityId) {
        for (int abilityIndex = 0; abilityIndex < abilities.size(); ++abilityIndex) {
            long abilityEntity = abilities.get(abilityIndex);

            if (isValidTarget(abilityEntity, targetEntityId))
            {
                return Optional.of(abilityIndex);
            }
        }

        return Optional.empty();
    }

    private boolean isValidTarget(long abilityEntityId, long targetEntityId) {
        AbilityComponent abilityComponent = entityManager.getComponent(abilityEntityId, AbilityComponent.class);
        GameplayTagsComponent gameplayTagsComponent =
                entityManager.getComponent(targetEntityId, GameplayTagsComponent.class);

        return gameplayTagsComponent.getInitialGameplayTags().containsAll(abilityComponent.getTargetRequiredTags()) &&
                gameplayTagsComponent.getInitialGameplayTags().stream().noneMatch(tag -> abilityComponent.getTargetBlockedTags().contains(tag));
    }
}
