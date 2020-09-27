package de.pinneddown.server.tests;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.GameEvent;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.components.ThreatModifierComponent;
import de.pinneddown.server.events.AbilityEffectAppliedEvent;
import de.pinneddown.server.events.AbilityEffectRemovedEvent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.events.ThreatModifiersChangedEvent;
import de.pinneddown.server.systems.ThreatSystem;
import de.pinneddown.server.util.ThreatUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class ThreatSystemTests {
    private HashMap<String, Integer> threatModifiers;

    @Test
    void addsInitialThreat() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        ThreatUtils threatUtils = new ThreatUtils(eventManager, entityManager);

        ThreatSystem system = new ThreatSystem(eventManager, entityManager, threatUtils);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        ThreatComponent threatComponent = entityManager.getComponent(1L, ThreatComponent.class);

        assertThat(threatComponent).isNotNull();
        assertThat(threatComponent.getThreat()).isGreaterThan(0);
    }

    @Test
    void appliesThreatModifiers() {
        // ARRANGE
        // Create system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        ThreatUtils threatUtils = new ThreatUtils(eventManager, entityManager);

        ThreatSystem system = new ThreatSystem(eventManager, entityManager, threatUtils);

        // Create effect.
        long effectEntityId = entityManager.createEntity();

        ThreatModifierComponent threatModifierComponent = new ThreatModifierComponent();
        HashMap<String, Integer> cardThreatModifiers = new HashMap<>();
        cardThreatModifiers.put("TestCard", +2);
        cardThreatModifiers.put("TestCard2", -3);
        threatModifierComponent.setThreatModifiers(cardThreatModifiers);
        entityManager.addComponent(effectEntityId, threatModifierComponent);

        // Register for events.
        eventManager.addEventHandler(EventType.THREAT_MODIFIERS_CHANGED, this::onThreatModifiersChanged);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());
        eventManager.queueEvent(EventType.ABILITY_EFFECT_APPLIED, new AbilityEffectAppliedEvent(effectEntityId, 0L));

        // ASSERT
        assertThat(threatModifiers).isEqualTo(cardThreatModifiers);
    }

    @Test
    void removesThreatModifiers() {
        // ARRANGE
        // Create system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        ThreatUtils threatUtils = new ThreatUtils(eventManager, entityManager);

        ThreatSystem system = new ThreatSystem(eventManager, entityManager, threatUtils);

        // Create effect.
        long effectEntityId = entityManager.createEntity();

        ThreatModifierComponent threatModifierComponent = new ThreatModifierComponent();
        HashMap<String, Integer> cardThreatModifiers = new HashMap<>();
        cardThreatModifiers.put("TestCard", +2);
        cardThreatModifiers.put("TestCard2", -3);
        threatModifierComponent.setThreatModifiers(cardThreatModifiers);
        entityManager.addComponent(effectEntityId, threatModifierComponent);

        // Register for events.
        eventManager.addEventHandler(EventType.THREAT_MODIFIERS_CHANGED, this::onThreatModifiersChanged);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());
        eventManager.queueEvent(EventType.ABILITY_EFFECT_APPLIED, new AbilityEffectAppliedEvent(effectEntityId, 0L));
        eventManager.queueEvent(EventType.ABILITY_EFFECT_REMOVED, new AbilityEffectRemovedEvent(effectEntityId, 0L));

        // ASSERT
        assertThat(threatModifiers).isEmpty();
    }

    private void onThreatModifiersChanged(GameEvent gameEvent) {
        ThreatModifiersChangedEvent eventData = (ThreatModifiersChangedEvent)gameEvent.getEventData();
        threatModifiers = eventData.getThreatModifiers();
    }
}
