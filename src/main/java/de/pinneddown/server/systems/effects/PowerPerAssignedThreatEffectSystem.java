package de.pinneddown.server.systems.effects;

import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.AbilityEffectActivatedEvent;
import de.pinneddown.server.events.AbilityEffectDeactivatedEvent;
import de.pinneddown.server.util.PowerUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class PowerPerAssignedThreatEffectSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private PowerUtils powerUtils;

    private HashSet<Long> powerPerAssignedThreatEffects;

    public PowerPerAssignedThreatEffectSystem(EventManager eventManager, EntityManager entityManager, PowerUtils powerUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.powerUtils = powerUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::onAbilityEffectActivated);
        this.eventManager.addEventHandler(EventType.STARSHIP_ASSIGNED, this::onStarshipAssigned);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_DEACTIVATED, this::onAbilityEffectDeactivated);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        powerPerAssignedThreatEffects = new HashSet<>();
    }

    private void onAbilityEffectActivated(GameEvent gameEvent) {
        AbilityEffectActivatedEvent eventData = (AbilityEffectActivatedEvent)gameEvent.getEventData();
        applyPowerPerAssignedThreat(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 1);
    }

    private void onStarshipAssigned(GameEvent gameEvent) {
        // Update effects.
        for (long entityId : powerPerAssignedThreatEffects) {
            AbilityEffectComponent abilityEffectComponent =
                    entityManager.getComponent(entityId, AbilityEffectComponent.class);
            applyPowerPerAssignedThreat(entityId, abilityEffectComponent.getTargetEntityId(), 1);
        }
    }

    private void onAbilityEffectDeactivated(GameEvent gameEvent) {
        AbilityEffectDeactivatedEvent eventData = (AbilityEffectDeactivatedEvent)gameEvent.getEventData();

        applyPowerPerAssignedThreat(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 0);

        powerPerAssignedThreatEffects.remove(eventData.getEffectEntityId());
    }

    private void applyPowerPerAssignedThreat(long effectEntityId, long targetEntityId, int factor) {
        // Check if we need to watch that effect.
        AbilityEffectComponent abilityEffectComponent =
                entityManager.getComponent(effectEntityId, AbilityEffectComponent.class);

        if (abilityEffectComponent.getDuration() == AbilityEffectDuration.INDEFINITE) {
            powerPerAssignedThreatEffects.add(effectEntityId);
        }

        // Update power bonus.
        PowerPerAssignedThreatComponent powerPerAssignedThreatComponent =
                entityManager.getComponent(effectEntityId, PowerPerAssignedThreatComponent.class);
        PowerComponent targetPowerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);

        if (powerPerAssignedThreatComponent == null || targetPowerComponent == null) {
            return;
        }

        AssignmentComponent assignmentComponent = entityManager.getComponent(targetEntityId, AssignmentComponent.class);
        ThreatComponent threatComponent = assignmentComponent != null
                ? entityManager.getComponent(assignmentComponent.getAssignedTo(), ThreatComponent.class)
                : null;
        int threat = threatComponent != null ? threatComponent.getThreat() : 0;

        int oldPowerModifier = targetPowerComponent.getPowerModifier();
        int newPowerModifier = oldPowerModifier
                - powerPerAssignedThreatComponent.getAppliedPowerPerThreat()
                + (threat * factor);

        powerPerAssignedThreatComponent.setAppliedPowerPerThreat(threat * factor);

        powerUtils.setPowerModifier(targetEntityId, newPowerModifier);
    }
}
