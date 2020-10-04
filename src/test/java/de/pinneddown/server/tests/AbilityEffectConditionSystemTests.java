package de.pinneddown.server.tests;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.GameEvent;
import de.pinneddown.server.components.AssignmentComponent;
import de.pinneddown.server.components.PowerComponent;
import de.pinneddown.server.components.PowerDifferenceConditionComponent;
import de.pinneddown.server.events.AbilityEffectAppliedEvent;
import de.pinneddown.server.systems.AbilityEffectConditionSystem;
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

    private void applyEffectWithPowerDifferenceConditionAndAssertEffectApplied(int targetPower, int assignedToPower, boolean assertActivated) {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        AbilityEffectConditionSystem system = new AbilityEffectConditionSystem(eventManager, entityManager);

        eventManager.queueEvent(EventType.READY_TO_START, null);

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
