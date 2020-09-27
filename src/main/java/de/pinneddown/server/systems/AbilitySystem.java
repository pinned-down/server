package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ActivateAbilityAction;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.*;
import de.pinneddown.server.util.PowerUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;

@Component
public class AbilitySystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;
    private PowerUtils powerUtils;

    private int totalLocations;

    private HashSet<Long> powerPerLocationEffects;
    private HashSet<Long> powerPerAssignedThreatEffects;

    public AbilitySystem(EventManager eventManager, EntityManager entityManager, BlueprintManager blueprintManager,
                         PowerUtils powerUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;
        this.powerUtils = powerUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(ActionType.ACTIVATE_ABILITY, this::onActivateAbility);
        this.eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);
        this.eventManager.addEventHandler(EventType.CARD_REMOVED, this::onCardRemoved);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_REMOVED, this::onAbilityEffectRemoved);
        this.eventManager.addEventHandler(EventType.CURRENT_LOCATION_CHANGED, this::onCurrentLocationChanged);
        this.eventManager.addEventHandler(EventType.STARSHIP_ASSIGNED, this::onStarshipAssigned);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        totalLocations = 0;

        powerPerLocationEffects = new HashSet<>();
        powerPerAssignedThreatEffects = new HashSet<>();
    }

    private void onActivateAbility(GameEvent gameEvent) {
        ActivateAbilityAction eventData = (ActivateAbilityAction)gameEvent.getEventData();

        AbilitiesComponent abilitiesComponent =
                entityManager.getComponent(eventData.getEntityId(), AbilitiesComponent.class);
        ArrayList<Long> abilityEntities = abilitiesComponent.getOrCreateAbilityEntities(blueprintManager);
        long abilityEntityId = abilityEntities.get(eventData.getAbilityIndex());

        activateAbility(abilityEntityId, eventData.getTargetEntityId());
    }

    private void onCardPlayed(GameEvent gameEvent) {
        CardPlayedEvent cardPlayedEvent = (CardPlayedEvent)gameEvent.getEventData();
        long entityId = cardPlayedEvent.getEntityId();

        AbilitiesComponent abilitiesComponent =
                entityManager.getComponent(entityId, AbilitiesComponent.class);

        if (abilitiesComponent == null) {
            return;
        }

        ArrayList<Long> abilityEntities = abilitiesComponent.getOrCreateAbilityEntities(blueprintManager);

        for (long abilityEntityId : abilityEntities) {
            AbilityComponent abilityComponent = entityManager.getComponent(abilityEntityId, AbilityComponent.class);

            if (!TargetType.PASSIVE.equals(abilityComponent.getTargetType())) {
                continue;
            }

            activateAbility(abilityEntityId, entityId);
        }
    }

    private void onAbilityEffectRemoved(GameEvent gameEvent) {
        AbilityEffectRemovedEvent eventData = (AbilityEffectRemovedEvent)gameEvent.getEventData();

        // Remove power bonus.
        applyPowerBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), -1);
        applyPowerPerLocationBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 0);
        applyPowerPerAssignedThreat(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 0);

        powerPerLocationEffects.remove(eventData.getEffectEntityId());
        powerPerAssignedThreatEffects.remove(eventData.getEffectEntityId());
    }

    private void onCardRemoved(GameEvent gameEvent) {
        CardRemovedEvent eventData = (CardRemovedEvent)gameEvent.getEventData();

        // Remove ability entities.
        AbilitiesComponent abilitiesComponent =
                entityManager.getComponent(eventData.getEntityId(), AbilitiesComponent.class);

        if (abilitiesComponent == null) {
            return;
        }

        ArrayList<Long> abilities = abilitiesComponent.getAbilityEntities();

        if (abilities == null) {
            return;
        }

        abilities.forEach(abilityEntity -> entityManager.removeEntity(abilityEntity));
    }

    private void onCurrentLocationChanged(GameEvent gameEvent) {
        ++totalLocations;

        // Update effects.
        for (long entityId : powerPerLocationEffects) {
            AbilityEffectComponent abilityEffectComponent = entityManager.getComponent(entityId, AbilityEffectComponent.class);
            applyPowerPerLocationBonus(entityId, abilityEffectComponent.getTargetEntityId(), 1);
        }
    }

    private void onStarshipAssigned(GameEvent gameEvent) {
        StarshipAssignedEvent eventData = (StarshipAssignedEvent)gameEvent.getEventData();

        // Update effects.
        for (long entityId : powerPerAssignedThreatEffects) {
            AbilityEffectComponent abilityEffectComponent = entityManager.getComponent(entityId, AbilityEffectComponent.class);
            applyPowerPerAssignedThreat(entityId, abilityEffectComponent.getTargetEntityId(), 1);
        }
    }

    private void activateAbility(long abilityEntityId, long targetEntityId) {
        AbilityComponent abilityComponent = entityManager.getComponent(abilityEntityId, AbilityComponent.class);

        // Apply effects.
        for (String effectBlueprintId : abilityComponent.getAbilityEffects()) {
            long effectEntityId = blueprintManager.createEntity(effectBlueprintId);

            // Check conditions.
            if (!checkPowerDifferenceCondition(effectEntityId, targetEntityId)) {
                continue;
            }

            // Apply overloads.
            applyOverloads(effectEntityId, targetEntityId);

            // Apply power bonus.
            applyPowerBonus(effectEntityId, targetEntityId, 1);
            applyPowerPerLocationBonus(effectEntityId, targetEntityId, 1);
            applyPowerPerAssignedThreat(effectEntityId, targetEntityId, 1);

            // Store target.
            AbilityEffectComponent abilityEffectComponent =
                    entityManager.getComponent(effectEntityId, AbilityEffectComponent.class);

            if (abilityEffectComponent != null) {
                abilityEffectComponent.setTargetEntityId(targetEntityId);
            }

            // Notify listeners.
            AbilityEffectAppliedEvent abilityEffectAppliedEvent =
                    new AbilityEffectAppliedEvent(effectEntityId, targetEntityId);
            eventManager.queueEvent(EventType.ABILITY_EFFECT_APPLIED, abilityEffectAppliedEvent);
        }
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

    private void applyPowerBonus(long effectEntityId, long targetEntityId, int powerFactor) {
        PowerComponent effectPowerComponent = entityManager.getComponent(effectEntityId, PowerComponent.class);
        PowerComponent targetPowerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);

        if (effectPowerComponent != null && targetPowerComponent != null) {
            int oldPowerModifier = targetPowerComponent.getPowerModifier();
            int newPowerModifier = oldPowerModifier + (effectPowerComponent.getPowerModifier() * powerFactor);

            powerUtils.setPowerModifier(targetEntityId, newPowerModifier);
        }
    }

    private void applyPowerPerLocationBonus(long effectEntityId, long targetEntityId, int powerFactor) {
        powerPerLocationEffects.add(effectEntityId);

        PowerPerLocationComponent powerPerLocationComponent = entityManager.getComponent(effectEntityId, PowerPerLocationComponent.class);
        PowerComponent targetPowerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);

        if (powerPerLocationComponent != null && targetPowerComponent != null) {
            int oldPowerModifier = targetPowerComponent.getPowerModifier();
            int newPowerModifier = oldPowerModifier
                    - powerPerLocationComponent.getAppliedPowerPerLocation()
                    + (totalLocations * powerFactor);

            powerPerLocationComponent.setAppliedPowerPerLocation(totalLocations * powerFactor);

            powerUtils.setPowerModifier(targetEntityId, newPowerModifier);
        }
    }

    private void applyPowerPerAssignedThreat(long effectEntityId, long targetEntityId, int factor) {
        powerPerAssignedThreatEffects.add(effectEntityId);

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

    private void applyOverloads(long effectEntityId, long targetEntityId) {
        OverloadComponent overloadComponent = entityManager.getComponent(effectEntityId, OverloadComponent.class);

        if (overloadComponent != null) {
            for (int i = 0; i < overloadComponent.getOverloads(); ++i) {
                eventManager.queueEvent(EventType.STARSHIP_OVERLOADED, new StarshipOverloadedEvent(targetEntityId));
            }
        }
    }
}
