package de.pinneddown.server.systems.effects;

import de.pinneddown.server.*;
import de.pinneddown.server.components.AbilityEffectComponent;
import de.pinneddown.server.components.PowerComponent;
import de.pinneddown.server.components.PowerPerLocationComponent;
import de.pinneddown.server.events.AbilityEffectActivatedEvent;
import de.pinneddown.server.events.AbilityEffectDeactivatedEvent;
import de.pinneddown.server.util.PowerUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class PowerPerLocationEffectSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private PowerUtils powerUtils;

    private int totalLocations;
    private HashSet<Long> powerPerLocationEffects;

    public PowerPerLocationEffectSystem(EventManager eventManager, EntityManager entityManager, PowerUtils powerUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.powerUtils = powerUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::onAbilityEffectActivated);
        this.eventManager.addEventHandler(EventType.CURRENT_LOCATION_CHANGED, this::onCurrentLocationChanged);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_DEACTIVATED, this::onAbilityEffectDeactivated);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        totalLocations = 0;
        powerPerLocationEffects = new HashSet<>();
    }

    private void onAbilityEffectActivated(GameEvent gameEvent) {
        AbilityEffectActivatedEvent eventData = (AbilityEffectActivatedEvent)gameEvent.getEventData();
        applyPowerPerLocationBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 1);
    }

    private void onCurrentLocationChanged(GameEvent gameEvent) {
        ++totalLocations;

        // Update effects.
        for (long entityId : powerPerLocationEffects) {
            AbilityEffectComponent abilityEffectComponent =
                    entityManager.getComponent(entityId, AbilityEffectComponent.class);
            applyPowerPerLocationBonus(entityId, abilityEffectComponent.getTargetEntityId(), 1);
        }
    }

    private void onAbilityEffectDeactivated(GameEvent gameEvent) {
        AbilityEffectDeactivatedEvent eventData = (AbilityEffectDeactivatedEvent)gameEvent.getEventData();

        applyPowerPerLocationBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 0);

        powerPerLocationEffects.remove(eventData.getEffectEntityId());
    }

    private void applyPowerPerLocationBonus(long effectEntityId, long targetEntityId, int powerFactor) {
        // Update power bonus.
        PowerPerLocationComponent powerPerLocationComponent =
                entityManager.getComponent(effectEntityId, PowerPerLocationComponent.class);
        PowerComponent targetPowerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);

        if (powerPerLocationComponent == null || targetPowerComponent == null) {
            return;
        }

        int oldPowerModifier = targetPowerComponent.getPowerModifier();
        int newPowerModifier = oldPowerModifier
                - powerPerLocationComponent.getAppliedPowerPerLocation()
                + (totalLocations * powerFactor);

        powerPerLocationComponent.setAppliedPowerPerLocation(totalLocations * powerFactor);

        powerUtils.setPowerModifier(targetEntityId, newPowerModifier);

        // Check if we need to watch that effect.
        AbilityEffectComponent abilityEffectComponent =
                entityManager.getComponent(effectEntityId, AbilityEffectComponent.class);

        if (abilityEffectComponent.getDuration() == AbilityEffectDuration.INDEFINITE) {
            powerPerLocationEffects.add(effectEntityId);
        }
    }
}
