package de.pinneddown.server.util;

import de.pinneddown.server.*;
import de.pinneddown.server.components.BlueprintComponent;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.events.ThreatChangedEvent;
import de.pinneddown.server.events.ThreatModifiersChangedEvent;
import de.pinneddown.server.events.ThreatPoolInitializedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ThreatUtils {
    private EventManager eventManager;
    private EntityManager entityManager;

    private long threatPoolEntityId;
    private HashMap<String, Integer> threatModifiers;

    public ThreatUtils(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.THREAT_POOL_INITIALIZED, this::onThreatPoolInitialized);
        this.eventManager.addEventHandler(EventType.THREAT_MODIFIERS_CHANGED, this::onThreatModifiersChanged);
    }

    public int getThreat() {
        ThreatComponent threatPoolThreatComponent = entityManager.getComponent(threatPoolEntityId, ThreatComponent.class);
        return threatPoolThreatComponent.getThreat();
    }

    public int getThreat(long entityId) {
        ThreatComponent threatComponent = entityManager.getComponent(entityId, ThreatComponent.class);
        int threat = threatComponent != null ? threatComponent.getThreat() : 0;

        BlueprintComponent blueprintComponent = entityManager.getComponent(entityId, BlueprintComponent.class);
        int threatModifier = threatModifiers.getOrDefault(blueprintComponent.getBlueprintId(), 0);

        return threat + threatModifier;
    }

    public void addThreat(int additionalThreat) {
        setThreat(getThreat() + additionalThreat);
    }

    public void setThreat(int newThreat) {
        setThreat(this.threatPoolEntityId, newThreat);
    }

    public void setThreat(long threatPoolEntityId, int newThreat) {
        ThreatComponent threatPoolThreatComponent = entityManager.getComponent(threatPoolEntityId, ThreatComponent.class);

        if (threatPoolThreatComponent.getThreat() == newThreat) {
            return;
        }

        threatPoolThreatComponent.setThreat(newThreat);

        ThreatChangedEvent threatChangedEvent = new ThreatChangedEvent(newThreat);
        eventManager.queueEvent(EventType.THREAT_CHANGED, threatChangedEvent);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        threatModifiers = new HashMap<>();
    }

    private void onThreatPoolInitialized(GameEvent gameEvent) {
        ThreatPoolInitializedEvent eventData = (ThreatPoolInitializedEvent)gameEvent.getEventData();
        threatPoolEntityId = eventData.getEntityId();
    }

    private void onThreatModifiersChanged(GameEvent gameEvent) {
        ThreatModifiersChangedEvent eventData = (ThreatModifiersChangedEvent)gameEvent.getEventData();
        threatModifiers = eventData.getThreatModifiers();
    }

}
