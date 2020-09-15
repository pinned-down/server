package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.events.ThreatChangedEvent;
import de.pinneddown.server.events.ThreatPoolInitializedEvent;
import de.pinneddown.server.util.ThreatUtils;
import org.springframework.stereotype.Component;

@Component
public class ThreatSystem {
    private static final int INITIAL_THREAT = 2;

    private EventManager eventManager;
    private EntityManager entityManager;
    private ThreatUtils threatUtils;

    public ThreatSystem(EventManager eventManager, EntityManager entityManager, ThreatUtils threatUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.threatUtils = threatUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        // Set up threat pool.
        long threatPoolEntityId = entityManager.createEntity();

        ThreatComponent threatComponent = new ThreatComponent();
        entityManager.addComponent(threatPoolEntityId, threatComponent);

        // Notify listeners.
        ThreatPoolInitializedEvent eventData = new ThreatPoolInitializedEvent();
        eventData.setEntityId(threatPoolEntityId);

        eventManager.queueEvent(EventType.THREAT_POOL_INITIALIZED, eventData);

        // Set initial threat.
        threatUtils.setThreat(threatPoolEntityId, INITIAL_THREAT);
    }
}
