package de.pinneddown.server.util;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.GameEvent;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.events.ThreatChangedEvent;
import de.pinneddown.server.events.ThreatPoolInitializedEvent;
import org.springframework.stereotype.Component;

@Component
public class ThreatUtils {
    private EventManager eventManager;
    private EntityManager entityManager;

    private long threatPoolEntityId;

    public ThreatUtils(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        this.eventManager.addEventHandler(EventType.THREAT_POOL_INITIALIZED, this::onThreatPoolInitialized);
    }

    public int getThreat() {
        ThreatComponent threatPoolThreatComponent = entityManager.getComponent(threatPoolEntityId, ThreatComponent.class);
        return threatPoolThreatComponent.getThreat();
    }

    public void setThreat(int newThreat) {
        setThreat(this.threatPoolEntityId, newThreat);
    }

    public void setThreat(long threatPoolEntityId, int newThreat) {
        ThreatComponent threatPoolThreatComponent = entityManager.getComponent(threatPoolEntityId, ThreatComponent.class);
        threatPoolThreatComponent.setThreat(newThreat);

        ThreatChangedEvent threatChangedEvent = new ThreatChangedEvent(newThreat);
        eventManager.queueEvent(EventType.THREAT_CHANGED, threatChangedEvent);
    }

    private void onThreatPoolInitialized(GameEvent gameEvent) {
        ThreatPoolInitializedEvent eventData = (ThreatPoolInitializedEvent)gameEvent.getEventData();
        threatPoolEntityId = eventData.getEntityId();
    }
}
