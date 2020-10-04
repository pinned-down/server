package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.AbilityEffectComponent;
import de.pinneddown.server.components.AssignmentComponent;
import de.pinneddown.server.components.PowerComponent;
import de.pinneddown.server.components.PowerDifferenceConditionComponent;
import de.pinneddown.server.events.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AbilityEffectConditionSystem {
    private EventManager eventManager;
    private EntityManager entityManager;

    private HashMap<Long, Long> indefiniteEffects;

    public AbilityEffectConditionSystem(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);
        this.eventManager.addEventHandler(EventType.STARSHIP_ASSIGNED, this::onStarshipAssigned);
        this.eventManager.addEventHandler(EventType.STARSHIP_POWER_CHANGED, this::onStarshipPowerChanged);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        indefiniteEffects = new HashMap<>();
    }

    private void onAbilityEffectApplied(GameEvent gameEvent) {
        AbilityEffectAppliedEvent eventData = (AbilityEffectAppliedEvent)gameEvent.getEventData();

        // Check if we need to watch that effect.
        AbilityEffectComponent abilityEffectComponent =
                entityManager.getComponent(eventData.getEffectEntityId(), AbilityEffectComponent.class);

        if (abilityEffectComponent.getDuration() == AbilityEffectDuration.INDEFINITE) {
            indefiniteEffects.put(eventData.getEffectEntityId(), eventData.getTargetEntityId());
        }

        checkConditionsAndApplyEffect(eventData.getEffectEntityId(), eventData.getTargetEntityId());
    }

    private void onStarshipAssigned(GameEvent gameEvent) {
        StarshipAssignedEvent eventData = (StarshipAssignedEvent)gameEvent.getEventData();
        checkConditionsAndApplyEffect(eventData.getAssignedStarship());
    }

    private void onStarshipPowerChanged(GameEvent gameEvent) {
        StarshipPowerChangedEvent eventData = (StarshipPowerChangedEvent)gameEvent.getEventData();
        checkConditionsAndApplyEffect(eventData.getEntityId());
    }

    private void checkConditionsAndApplyEffect(long effectEntityId) {
        for (Map.Entry<Long, Long> indefiniteEffect : indefiniteEffects.entrySet()) {
            if (effectEntityId == indefiniteEffect.getValue()) {
                checkConditionsAndApplyEffect(indefiniteEffect.getKey(), indefiniteEffect.getValue());
                return;
            }
        }
    }

    private void checkConditionsAndApplyEffect(long effectEntityId, long targetEntityId) {
        // Check conditions.
        boolean allConditionsFulfilled = checkConditions(effectEntityId, targetEntityId);

        AbilityEffectComponent abilityEffectComponent =
                entityManager.getComponent(effectEntityId, AbilityEffectComponent.class);

        if (allConditionsFulfilled && !abilityEffectComponent.isActive()) {
            // Activate effect.
            abilityEffectComponent.setActive(true);

            eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                    new AbilityEffectActivatedEvent(effectEntityId, targetEntityId));
        } else if (!allConditionsFulfilled && abilityEffectComponent.isActive()) {
            // Deactivate effect.
            abilityEffectComponent.setActive(false);

            eventManager.queueEvent(EventType.ABILITY_EFFECT_DEACTIVATED,
                    new AbilityEffectDeactivatedEvent(effectEntityId, targetEntityId));
        }
    }

    private boolean checkConditions(long effectEntityId, long targetEntityId) {
        // Check conditions.
        if (!checkPowerDifferenceCondition(effectEntityId, targetEntityId)) {
            return false;
        }

        return true;
    }

    private boolean checkPowerDifferenceCondition(long effectEntityId, long targetEntityId) {
        PowerDifferenceConditionComponent powerDifferenceConditionComponent =
                entityManager.getComponent(effectEntityId, PowerDifferenceConditionComponent.class);

        if (powerDifferenceConditionComponent == null) {
            return true;
        }

        AssignmentComponent assignmentComponent = entityManager.getComponent(targetEntityId, AssignmentComponent.class);

        if (assignmentComponent == null) {
            return false;
        }

        long assignedTo = assignmentComponent.getAssignedTo();

        PowerComponent targetPowerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);
        PowerComponent assignedToPowerComponent = entityManager.getComponent(assignedTo, PowerComponent.class);

        if (targetPowerComponent == null || assignedToPowerComponent == null) {
            return false;
        }

        return targetPowerComponent.getCurrentPower() - assignedToPowerComponent.getCurrentPower() >=
                powerDifferenceConditionComponent.getRequiredPowerDifference();
    }
}
