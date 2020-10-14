package de.pinneddown.server.tests;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.TurnPhase;
import de.pinneddown.server.components.AbilityEffectComponent;
import de.pinneddown.server.events.AbilityEffectAppliedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.systems.AbilityEffectDurationSystem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbilityEffectDurationSystemTests {
    @Test
    void removesEffectAtEndOfFight() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        AbilityEffectDurationSystem system = new AbilityEffectDurationSystem(eventManager, entityManager);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        // Create effect.
        long effectEntityId = entityManager.createEntity();

        AbilityEffectComponent abilityEffectComponent = new AbilityEffectComponent();
        abilityEffectComponent.setAbilityEffectDuration("EndOfFight");

        entityManager.addComponent(effectEntityId, abilityEffectComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        // Apply effect.
        eventManager.queueEvent(EventType.ABILITY_EFFECT_APPLIED,
                new AbilityEffectAppliedEvent(effectEntityId, targetEntityId));

        // ACT
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.JUMP));

        // ASSERT
        assertThat(entityManager.getComponent(effectEntityId, AbilityEffectComponent.class)).isNull();
    }
}
