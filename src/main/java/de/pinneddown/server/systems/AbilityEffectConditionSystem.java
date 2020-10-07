package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.*;
import de.pinneddown.server.util.GameplayTagUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class AbilityEffectConditionSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private GameplayTagUtils gameplayTagUtils;

    private HashMap<Long, Long> indefiniteEffects;
    private ArrayList<Long> starshipEntities;

    public AbilityEffectConditionSystem(EventManager eventManager, EntityManager entityManager,
                                        GameplayTagUtils gameplayTagUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.gameplayTagUtils = gameplayTagUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);
        this.eventManager.addEventHandler(EventType.STARSHIP_ASSIGNED, this::onStarshipAssigned);
        this.eventManager.addEventHandler(EventType.STARSHIP_POWER_CHANGED, this::onStarshipPowerChanged);
        this.eventManager.addEventHandler(EventType.GLOBAL_GAMEPLAY_TAGS_CHANGED, this::onGlobalGameplayTagsChanged);
        this.eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);
        this.eventManager.addEventHandler(EventType.CARD_REMOVED, this::onCardRemoved);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        indefiniteEffects = new HashMap<>();
        starshipEntities = new ArrayList<>();
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

    private void onGlobalGameplayTagsChanged(GameEvent gameEvent) {
        updateAllEffects();
    }

    private void onCardPlayed(GameEvent gameEvent) {
        CardPlayedEvent eventData = (CardPlayedEvent)gameEvent.getEventData();

        if (gameplayTagUtils.hasGameplayTag(eventData.getEntityId(), GameplayTags.CARDTYPE_STARSHIP)) {
            starshipEntities.add(eventData.getEntityId());
        }

        updateAllEffects();
    }

    private void onCardRemoved(GameEvent gameEvent) {
        CardRemovedEvent eventData = (CardRemovedEvent)gameEvent.getEventData();

        if (gameplayTagUtils.hasGameplayTag(eventData.getEntityId(), GameplayTags.CARDTYPE_STARSHIP)) {
            starshipEntities.remove(eventData.getEntityId());
        }

        updateAllEffects();
    }

    private void updateAllEffects() {
        for (Map.Entry<Long, Long> indefiniteEffect : indefiniteEffects.entrySet()) {
            checkConditionsAndApplyEffect(indefiniteEffect.getKey(), indefiniteEffect.getValue());
        }
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

            BlueprintComponent blueprintComponent = entityManager.getComponent(effectEntityId, BlueprintComponent.class);
            String blueprintId = blueprintComponent != null ? blueprintComponent.getBlueprintId() : null;

            InstigatorComponent instigatorComponent = entityManager.getComponent(effectEntityId, InstigatorComponent.class);
            long instigatorEntityId = instigatorComponent != null ? instigatorComponent.getEntityId() : EntityManager.INVALID_ENTITY;
            BlueprintComponent instigatorBlueprintComponent = instigatorEntityId != EntityManager.INVALID_ENTITY
                    ? entityManager.getComponent(instigatorEntityId, BlueprintComponent.class)
                    : null;
            String instigatorBlueprintId = instigatorBlueprintComponent != null ? instigatorBlueprintComponent.getBlueprintId() : null;

            eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                    new AbilityEffectActivatedEvent(effectEntityId, blueprintId, instigatorBlueprintId, targetEntityId));
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

        if (!checkTargetGameplayTagsCondition(effectEntityId, targetEntityId)) {
            return false;
        }

        if (!checkFleetSizeCondition(effectEntityId, targetEntityId)) {
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

    private boolean checkTargetGameplayTagsCondition(long effectEntityId, long targetEntityId) {
        TargetGameplayTagsConditionComponent targetGameplayTagsConditionComponent =
                entityManager.getComponent(effectEntityId, TargetGameplayTagsConditionComponent.class);

        if (targetGameplayTagsConditionComponent == null) {
            return true;
        }

        return gameplayTagUtils.matchesTagRequirements(
                targetEntityId,
                targetGameplayTagsConditionComponent.getTargetRequiredTags(),
                targetGameplayTagsConditionComponent.getTargetBlockedTags());
    }

    private boolean checkFleetSizeCondition(long effectEntityId, long targetEntityId) {
        FleetSizeConditionComponent fleetSizeConditionComponent =
                entityManager.getComponent(effectEntityId, FleetSizeConditionComponent.class);

        if (fleetSizeConditionComponent == null) {
            return true;
        }

        // Get fleet size.
        OwnerComponent effectTargetOwnerComponent = entityManager.getComponent(targetEntityId, OwnerComponent.class);

        if (effectTargetOwnerComponent == null) {
            return true;
        }

        int fleetSize = 0;

        for (long starshipEntityId : starshipEntities) {
            OwnerComponent starshipOwnerComponent = entityManager.getComponent(starshipEntityId, OwnerComponent.class);

            if (starshipOwnerComponent != null && starshipOwnerComponent.getOwner() == effectTargetOwnerComponent.getOwner()) {
                ++fleetSize;
            }
        }

        return fleetSize >= fleetSizeConditionComponent.getMinFleetSize() &&
                fleetSize <= fleetSizeConditionComponent.getMaxFleetSize();
    }
}
