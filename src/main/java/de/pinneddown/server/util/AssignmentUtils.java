package de.pinneddown.server.util;

import de.pinneddown.server.EntityComponent;
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

        if (assignmentComponent.getAssignedTo() != EntityManager.INVALID_ENTITY) {
            // Remove old assignment.
            AssignmentComponent assignedToAssignmentComponent =
                    entityManager.getComponent(assignmentComponent.getAssignedTo(), AssignmentComponent.class);
            assignedToAssignmentComponent.setAssignedTo(EntityManager.INVALID_ENTITY);
        }

        assignmentComponent.setAssignedTo(assignedTo);

        if (assignedTo != EntityManager.INVALID_ENTITY) {
            // Set new assignment.
            AssignmentComponent assignedToAssignmentComponent = entityManager.getComponent(assignedTo,
                    AssignmentComponent.class);
            assignedToAssignmentComponent.setAssignedTo(assignedEntityId);
        }

        // Notify listeners.
        StarshipAssignedEvent starshipAssignedEvent =
                new StarshipAssignedEvent(assignedEntityId, assignedTo);

        eventManager.queueEvent(EventType.STARSHIP_ASSIGNED, starshipAssignedEvent);
    }
}
