package de.pinneddown.server.systems.effects;

import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.AbilityEffectActivatedEvent;
import de.pinneddown.server.events.AbilityEffectDeactivatedEvent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.util.GameplayTagUtils;
import de.pinneddown.server.util.PowerUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class PowerPerFleetSizeEffectSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private PowerUtils powerUtils;
    private GameplayTagUtils gameplayTagUtils;

    private HashSet<Long> starshipEntities;
    private HashSet<Long> powerPerFleetSizeEffects;

    public PowerPerFleetSizeEffectSystem(EventManager eventManager, EntityManager entityManager, PowerUtils powerUtils,
                                         GameplayTagUtils gameplayTagUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.powerUtils = powerUtils;
        this.gameplayTagUtils = gameplayTagUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::onAbilityEffectActivated);
        this.eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_DEACTIVATED, this::onAbilityEffectDeactivated);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        starshipEntities = new HashSet<>();
        powerPerFleetSizeEffects = new HashSet<>();
    }

    private void onAbilityEffectActivated(GameEvent gameEvent) {
        AbilityEffectActivatedEvent eventData = (AbilityEffectActivatedEvent)gameEvent.getEventData();
        applyPowerPerFleetSizeBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 1);
    }

    private void onCardPlayed(GameEvent gameEvent) {
        CardPlayedEvent eventData = (CardPlayedEvent)gameEvent.getEventData();

        if (!gameplayTagUtils.hasGameplayTag(eventData.getEntityId(), GameplayTags.CARDTYPE_STARSHIP)) {
            return;
        }

        starshipEntities.add(eventData.getEntityId());

        // Update effects.
        for (long entityId : powerPerFleetSizeEffects) {
            AbilityEffectComponent abilityEffectComponent =
                    entityManager.getComponent(entityId, AbilityEffectComponent.class);
            applyPowerPerFleetSizeBonus(entityId, abilityEffectComponent.getTargetEntityId(), 1);
        }
    }

    private void onAbilityEffectDeactivated(GameEvent gameEvent) {
        AbilityEffectDeactivatedEvent eventData = (AbilityEffectDeactivatedEvent)gameEvent.getEventData();

        applyPowerPerFleetSizeBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 0);

        powerPerFleetSizeEffects.remove(eventData.getEffectEntityId());
    }

    private void applyPowerPerFleetSizeBonus(long effectEntityId, long targetEntityId, int factor) {
        // Update power bonus.
        PowerPerFleetSizeComponent powerPerFleetSizeComponent =
                entityManager.getComponent(effectEntityId, PowerPerFleetSizeComponent.class);
        PowerComponent targetPowerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);

        if (powerPerFleetSizeComponent == null || targetPowerComponent == null) {
            return;
        }

        int fleetSize = (int)starshipEntities.stream()
                .filter(entityId ->gameplayTagUtils.hasGameplayTag(entityId, powerPerFleetSizeComponent.getFleetGameplayTagFilter()))
                .count();

        int oldPowerModifier = targetPowerComponent.getPowerModifier();
        int newPowerModifier = oldPowerModifier
                - powerPerFleetSizeComponent.getAppliedPowerPerFleetSize()
                + (fleetSize * powerPerFleetSizeComponent.getPowerPerFleetSize() * factor);

        powerPerFleetSizeComponent.setAppliedPowerPerFleetSize
                (fleetSize * powerPerFleetSizeComponent.getPowerPerFleetSize() * factor);

        powerUtils.setPowerModifier(targetEntityId, newPowerModifier);

        // Check if we need to watch that effect.
        AbilityEffectComponent abilityEffectComponent =
                entityManager.getComponent(effectEntityId, AbilityEffectComponent.class);

        if (abilityEffectComponent.getAbilityEffectDurationEnum() == AbilityEffectDuration.INDEFINITE) {
            powerPerFleetSizeEffects.add(effectEntityId);
        }
    }
}
