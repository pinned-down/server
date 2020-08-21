package de.pinneddown.server.systems;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.GameEvent;
import de.pinneddown.server.components.ThreatComponent;
import org.springframework.stereotype.Component;

@Component
public class ThreatSystem {
    private static final int INITIAL_THREAT = 2;

    private EventManager eventManager;
    private EntityManager entityManager;

    private long threatPoolEntitId;

    public ThreatSystem(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        threatPoolEntitId = entityManager.createEntity();

        ThreatComponent threatComponent = new ThreatComponent();
        threatComponent.setThreat(INITIAL_THREAT);

        entityManager.addComponent(threatPoolEntitId, threatComponent);
    }
}
