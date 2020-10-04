package de.pinneddown.server.tests;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.AbilityEffectActivatedEvent;
import de.pinneddown.server.events.AbilityEffectDeactivatedEvent;
import de.pinneddown.server.systems.effects.PowerBonusEffectSystem;
import de.pinneddown.server.systems.effects.PowerPerAssignedThreatEffectSystem;
import de.pinneddown.server.systems.effects.PowerPerLocationEffectSystem;
import de.pinneddown.server.util.PowerUtils;
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
                new AbilityEffectActivatedEvent(effectEntityId, targetEntityId));

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
                new AbilityEffectActivatedEvent(effectEntityId, targetEntityId));

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
                new AbilityEffectActivatedEvent(effectEntityId, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isEqualTo(threatComponent.getThreat());
    }
}
