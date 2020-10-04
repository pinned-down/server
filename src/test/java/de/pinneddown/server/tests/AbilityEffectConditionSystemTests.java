package de.pinneddown.server.tests;

import com.google.common.collect.Lists;
import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.GameEvent;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.AbilityEffectAppliedEvent;
import de.pinneddown.server.systems.AbilityEffectConditionSystem;
import de.pinneddown.server.util.GameplayTagUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbilityEffectConditionSystemTests extends GameSystemTestSuite {
    private boolean effectActivated;

    @Test
    void applyEffectIfPowerDifferenceConditionFulfilled() {
        applyEffectWithPowerDifferenceConditionAndAssertEffectApplied(2, 1, true);
    }

    @Test
    void doesNotApplyEffectIfPowerDifferenceConditionNotFulfilled() {
        applyEffectWithPowerDifferenceConditionAndAssertEffectApplied(1, 2, false);
    }

    @Test
    void applyEffectIfTargetGameplayTagsConditionFulfilled() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        AbilityEffectConditionSystem system = createSystem(eventManager, entityManager);

        // Create effect.
        String testTag = "TestTag";

        long effectEntityId = createIndefiniteEffect(entityManager);
        TargetGameplayTagsConditionComponent targetGameplayTagsConditionComponent = new TargetGameplayTagsConditionComponent();
        targetGameplayTagsConditionComponent.setTargetRequiredTags(Lists.newArrayList(testTag));
        entityManager.addComponent(effectEntityId, targetGameplayTagsConditionComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();
        GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
        gameplayTagsComponent.setInitialGameplayTags(Lists.newArrayList(testTag));
        entityManager.addComponent(targetEntityId, gameplayTagsComponent);

        // Register for event.
        effectActivated = false;

        eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::onAbilityEffectActivated);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_APPLIED,
                new AbilityEffectAppliedEvent(effectEntityId, targetEntityId));

        // ASSERT
        assertThat(effectActivated).isTrue();
    }

    @Test
    void doesNotApplyEffectIfTargetGameplayTagsConditionIsNotFulfilled() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        AbilityEffectConditionSystem system = createSystem(eventManager, entityManager);

        // Create effect.
        String testTag = "TestTag";

        long effectEntityId = createIndefiniteEffect(entityManager);
        TargetGameplayTagsConditionComponent targetGameplayTagsConditionComponent = new TargetGameplayTagsConditionComponent();
        targetGameplayTagsConditionComponent.setTargetRequiredTags(Lists.newArrayList(testTag));
        entityManager.addComponent(effectEntityId, targetGameplayTagsConditionComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        // Register for event.
        effectActivated = false;

        eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::onAbilityEffectActivated);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_APPLIED,
                new AbilityEffectAppliedEvent(effectEntityId, targetEntityId));

        // ASSERT
        assertThat(effectActivated).isFalse();
    }

    private AbilityEffectConditionSystem createSystem(EventManager eventManager, EntityManager entityManager) {
        GameplayTagUtils gameplayTagUtils = new GameplayTagUtils(eventManager, entityManager);

        AbilityEffectConditionSystem system = new AbilityEffectConditionSystem(eventManager, entityManager, gameplayTagUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        return system;
    }

    private void applyEffectWithPowerDifferenceConditionAndAssertEffectApplied(int targetPower, int assignedToPower, boolean assertActivated) {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        AbilityEffectConditionSystem system = createSystem(eventManager, entityManager);

        // Create effect.
        long effectEntityId = createIndefiniteEffect(entityManager);
        PowerDifferenceConditionComponent powerDifferenceConditionComponent = new PowerDifferenceConditionComponent();
        powerDifferenceConditionComponent.setRequiredPowerDifference(1);
        entityManager.addComponent(effectEntityId, powerDifferenceConditionComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();
        PowerComponent powerComponent = new PowerComponent();
        powerComponent.setBasePower(targetPower);
        entityManager.addComponent(targetEntityId, powerComponent);

        // Create assigned starship.
        long assignedTo = entityManager.createEntity();
        PowerComponent assignedToPowerComponent = new PowerComponent();
        assignedToPowerComponent.setBasePower(assignedToPower);
        entityManager.addComponent(assignedTo, assignedToPowerComponent);

        AssignmentComponent assignmentComponent = new AssignmentComponent();
        assignmentComponent.setAssignedTo(assignedTo);
        entityManager.addComponent(targetEntityId, assignmentComponent);

        // Register for event.
        effectActivated = false;

        eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::onAbilityEffectActivated);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_APPLIED,
                new AbilityEffectAppliedEvent(effectEntityId, targetEntityId));

        // ASSERT
        assertThat(effectActivated).isEqualTo(assertActivated);
    }

    private void onAbilityEffectActivated(GameEvent gameEvent) {
        effectActivated = true;
    }
}
