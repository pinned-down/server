package de.pinneddown.server.util;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.components.AssignmentComponent;
import de.pinneddown.server.events.StarshipAssignedEvent;
import org.springframework.stereotype.Component;

@Component
public class AssignmentUtils {
    private EventManager eventManager;
    private EntityManager entityManager;

    public AssignmentUtils(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
    }

    public void assignTo(long assignedEntityId, long assignedTo) {
        AssignmentComponent assignmentComponent = entityManager.getComponent(assignedEntityId,
                AssignmentComponent.class);
        assignmentComponent.setAssignedTo(assignedTo);

        // Notify listeners.
        StarshipAssignedEvent starshipAssignedEvent =
                new StarshipAssignedEvent(assignedEntityId, assignedTo);

        eventManager.queueEvent(EventType.STARSHIP_ASSIGNED, starshipAssignedEvent);
    }
}
