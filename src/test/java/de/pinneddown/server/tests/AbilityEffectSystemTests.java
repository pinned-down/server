package de.pinneddown.server.tests;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.GameplayTags;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.AbilityEffectActivatedEvent;
import de.pinneddown.server.events.AbilityEffectDeactivatedEvent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.systems.effects.PowerBonusEffectSystem;
import de.pinneddown.server.systems.effects.PowerPerAssignedThreatEffectSystem;
import de.pinneddown.server.systems.effects.PowerPerFleetSizeEffectSystem;
import de.pinneddown.server.systems.effects.PowerPerLocationEffectSystem;
import de.pinneddown.server.util.GameplayTagUtils;
import de.pinneddown.server.util.PowerUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbilityEffectSystemTests extends GameSystemTestSuite {
    @Test
    void appliesPowerBonusEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);

        PowerBonusEffectSystem system = new PowerBonusEffectSystem(eventManager, entityManager, powerUtils);

        // Create effect.
        long effectEntityId = createIndefiniteEffect(entityManager);
        PowerBonusComponent powerBonusComponent = new PowerBonusComponent();
        powerBonusComponent.setPowerBonus(2);
        entityManager.addComponent(effectEntityId, powerBonusComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();
        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                new AbilityEffectActivatedEvent(effectEntityId, null, null, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isGreaterThan(0);
    }

    @Test
    void removesPowerEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);

        PowerBonusEffectSystem system = new PowerBonusEffectSystem(eventManager, entityManager, powerUtils);

        // Create effect.
        long effectEntityId = createIndefiniteEffect(entityManager);
        PowerBonusComponent powerBonusComponent = new PowerBonusComponent();
        powerBonusComponent.setPowerBonus(2);
        powerBonusComponent.setAppliedPowerBonus(2);
        entityManager.addComponent(effectEntityId, powerBonusComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();
        PowerComponent targetPowerComponent = new PowerComponent();
        targetPowerComponent.setPowerModifier(powerBonusComponent.getPowerBonus());

        entityManager.addComponent(targetEntityId, targetPowerComponent);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_DEACTIVATED,
                new AbilityEffectDeactivatedEvent(effectEntityId, targetEntityId));

        // ASSERT
        assertThat(targetPowerComponent.getPowerModifier()).isZero();
    }

    @Test
    void appliesPowerPerLocationEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);

        PowerPerLocationEffectSystem system = new PowerPerLocationEffectSystem(eventManager, entityManager, powerUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        // Create effect.
        long effectEntityId = createIndefiniteEffect(entityManager);
        PowerPerLocationComponent powerPerLocationComponent = new PowerPerLocationComponent();
        powerPerLocationComponent.setPowerPerLocation(1);
        entityManager.addComponent(effectEntityId, powerPerLocationComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();
        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        eventManager.queueEvent(EventType.CURRENT_LOCATION_CHANGED, null);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                new AbilityEffectActivatedEvent(effectEntityId, null, null, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isGreaterThan(0);
    }

    @Test
    void appliesPowerPerAssignedThreatEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);

        PowerPerAssignedThreatEffectSystem system =
                new PowerPerAssignedThreatEffectSystem(eventManager, entityManager, powerUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        // Create effect.
        long effectEntityId = createIndefiniteEffect(entityManager);
        PowerPerAssignedThreatComponent powerPerAssignedThreatComponent = new PowerPerAssignedThreatComponent();
        powerPerAssignedThreatComponent.setPowerPerThreat(1);
        entityManager.addComponent(effectEntityId, powerPerAssignedThreatComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        // Create assigned starship.
        long assignedTo = entityManager.createEntity();

        ThreatComponent threatComponent = new ThreatComponent();
        threatComponent.setThreat(2);
        entityManager.addComponent(assignedTo, threatComponent);

        AssignmentComponent assignmentComponent = new AssignmentComponent();
        assignmentComponent.setAssignedTo(assignedTo);
        entityManager.addComponent(targetEntityId, assignmentComponent);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                new AbilityEffectActivatedEvent(effectEntityId, null, null, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isEqualTo(threatComponent.getThreat());
    }

    @Test
    void appliesPowerPerFleetSizeEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);
        GameplayTagUtils gameplayTagUtils = new GameplayTagUtils(eventManager, entityManager);

        PowerPerFleetSizeEffectSystem system =
                new PowerPerFleetSizeEffectSystem(eventManager, entityManager, powerUtils, gameplayTagUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        // Create effect.
        String testFilterTag = "TestFilterTag";

        long effectEntityId = createIndefiniteEffect(entityManager);
        PowerPerFleetSizeComponent powerPerFleetSizeComponent = new PowerPerFleetSizeComponent();
        powerPerFleetSizeComponent.setPowerPerFleetSize(2);
        powerPerFleetSizeComponent.setFleetGameplayTagFilter(testFilterTag);
        entityManager.addComponent(effectEntityId, powerPerFleetSizeComponent);

        // Create fleet.
        int fleetSize = 3;

        for (int i = 0; i < fleetSize; ++i) {
            long fleetEntityId = entityManager.createEntity();
            GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
            gameplayTagsComponent.setInitialGameplayTags(Lists.newArrayList(GameplayTags.CARDTYPE_STARSHIP, testFilterTag));
            entityManager.addComponent(fleetEntityId, gameplayTagsComponent);

            eventManager.queueEvent(EventType.CARD_PLAYED, new CardPlayedEvent(fleetEntityId, null, 0L));
        }

        // Create target.
        long targetEntityId = entityManager.createEntity();
        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                new AbilityEffectActivatedEvent(effectEntityId, null, null, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isEqualTo(powerPerFleetSizeComponent.getPowerPerFleetSize() * fleetSize);
    }
}
