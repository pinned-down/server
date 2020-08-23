package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.events.ThreatPoolInitializedEvent;
import org.springframework.stereotype.Component;

@Component
public class ThreatSystem {
    private static final int INITIAL_THREAT = 2;

    private EventManager eventManager;
    private EntityManager entityManager;

    private long threatPoolEntityId;

    public ThreatSystem(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        threatPoolEntityId = entityManager.createEntity();

        ThreatComponent threatComponent = new ThreatComponent();
        threatComponent.setThreat(INITIAL_THREAT);

        entityManager.addComponent(threatPoolEntityId, threatComponent);

        // Notify listeners.
        ThreatPoolInitializedEvent eventData = new ThreatPoolInitializedEvent();
        eventData.setEntityId(threatPoolEntityId);

        eventManager.queueEvent(EventType.THREAT_POOL_INITIALIZED, eventData);
    }
}
