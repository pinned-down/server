package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ActivateAbilityAction;
import de.pinneddown.server.actions.PlayEffectAction;
import de.pinneddown.server.components.AbilitiesComponent;
import de.pinneddown.server.components.AbilityComponent;
import de.pinneddown.server.components.TargetGameplayTagsConditionComponent;
import de.pinneddown.server.events.CardRemovedEvent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import de.pinneddown.server.util.GameplayTagUtils;
import de.pinneddown.server.util.PlayerUtils;
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
    private GameplayTagUtils gameplayTagUtils;

    public PlayEffectSystem(EventManager eventManager, EntityManager entityManager, BlueprintManager blueprintManager,
                            PlayerUtils playerUtils, GameplayTagUtils gameplayTagUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;
        this.playerUtils = playerUtils;
        this.gameplayTagUtils = gameplayTagUtils;


        this.eventManager.addEventHandler(ActionType.PLAY_EFFECT, this::onPlayEffect);
    }

    private void onPlayEffect(GameEvent gameEvent) {
        PlayEffectAction eventData = (PlayEffectAction)gameEvent.getEventData();

        // Get player.
        long playerEntityId = playerUtils.getPlayerEntityId(eventData.getPlayerId());

        if (playerEntityId == EntityManager.INVALID_ENTITY) {
            return;
        }

        // Play card.
        long entityId = playerUtils.playCard(playerEntityId, eventData.getBlueprintId());

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
        TargetGameplayTagsConditionComponent targetGameplayTagsConditionComponent =
                entityManager.getComponent(abilityEntityId, TargetGameplayTagsConditionComponent.class);

        ArrayList<String> requiredTags = gameplayTagUtils.combineGameplayTags(abilityComponent.getRequiredTags(),
                targetGameplayTagsConditionComponent.getTargetRequiredTags());
        ArrayList<String> blockedTags = gameplayTagUtils.combineGameplayTags(abilityComponent.getBlockedTags(),
                targetGameplayTagsConditionComponent.getTargetBlockedTags());

        return gameplayTagUtils.matchesTagRequirements(targetEntityId,
                requiredTags, blockedTags);
    }
}
