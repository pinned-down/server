package de.pinneddown.server.tests;

import com.google.common.collect.Lists;
import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.AbilityEffectAppliedEvent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.systems.AbilityEffectConditionSystem;
import de.pinneddown.server.util.GameplayTagUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbilityEffectConditionSystemTests {
    private GameSystemTestUtils testUtils;
    private boolean effectActivated;

    @BeforeEach
    void beforeEach() {
        this.testUtils = new GameSystemTestUtils();
    }

    @Test
    void applyEffectIfPowerDifferenceConditionFulfilled() {
        activateEffectWithPowerDifferenceConditionAndAssertEffectApplied(2, 1, true);
    }

    @Test
    void doesNotApplyEffectIfPowerDifferenceConditionNotFulfilled() {
        activateEffectWithPowerDifferenceConditionAndAssertEffectApplied(1, 2, false);
    }

    @Test
    void applyEffectIfTargetGameplayTagsConditionFulfilled() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        AbilityEffectConditionSystem system = createSystem(eventManager, entityManager);

        // Create effect.
        String testTag = "TestTag";

        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
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

        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
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

    @Test
    void applyEffectIfFleetSizeConditionFulfilled() {
        activateEffectWithFleetSizeConditionAndAssertEffectApplied(2, 1,3,true);
    }

    @Test
    void doesNotApplyEffectIfFleetSizeTooSmall() {
        activateEffectWithFleetSizeConditionAndAssertEffectApplied(1, 2,3,false);
    }

    @Test
    void doesNotApplyEffectIfFleetSizeTooLarge() {
        activateEffectWithFleetSizeConditionAndAssertEffectApplied(3, 1,2,false);
    }

    private AbilityEffectConditionSystem createSystem(EventManager eventManager, EntityManager entityManager) {
        GameplayTagUtils gameplayTagUtils = new GameplayTagUtils(eventManager, entityManager);

        AbilityEffectConditionSystem system = new AbilityEffectConditionSystem(eventManager, entityManager, gameplayTagUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        return system;
    }

    private void activateEffectWithPowerDifferenceConditionAndAssertEffectApplied(int targetPower, int assignedToPower,
                                                                                  boolean assertActivated) {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        AbilityEffectConditionSystem system = createSystem(eventManager, entityManager);

        // Create effect.
        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
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

    private void activateEffectWithFleetSizeConditionAndAssertEffectApplied(int fleetSize, int minFleetSize,
                                                                            int maxFleetSize, boolean assertActivated) {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        AbilityEffectConditionSystem system = createSystem(eventManager, entityManager);

        // Create effect.
        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
        FleetSizeConditionComponent fleetSizeConditionComponent = new FleetSizeConditionComponent();
        fleetSizeConditionComponent.setMinFleetSize(minFleetSize);
        fleetSizeConditionComponent.setMaxFleetSize(maxFleetSize);
        entityManager.addComponent(effectEntityId, fleetSizeConditionComponent);

        // Create target.
        long targetEntityId = playStarship(entityManager, eventManager);

        // Create fleet.
        for (int i = 1; i < fleetSize; ++i) {
            playStarship(entityManager, eventManager);
        }

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

    private long playStarship(EntityManager entityManager, EventManager eventManager) {
        long entityId = entityManager.createEntity();

        OwnerComponent ownerComponent = new OwnerComponent();
        ownerComponent.setOwner(123L);
        entityManager.addComponent(entityId, ownerComponent);

        GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
        gameplayTagsComponent.setInitialGameplayTags(Lists.newArrayList(GameplayTags.CARDTYPE_STARSHIP));
        entityManager.addComponent(entityId, gameplayTagsComponent);

        eventManager.queueEvent(EventType.CARD_PLAYED, new CardPlayedEvent(entityId, null,
                ownerComponent.getOwner()));

        return entityId;
    }
}
