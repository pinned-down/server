package de.pinneddown.server.tests;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.systems.ThreatSystem;
import de.pinneddown.server.util.ThreatUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ThreatSystemTests {
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
}
