package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;
import de.pinneddown.server.EntityManager;

public class AssignmentComponent implements EntityComponent {
    private long assignedTo;

    public AssignmentComponent() {
        this.assignedTo = EntityManager.INVALID_ENTITY;
    }

    public long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(long assignedTo) {
        this.assignedTo = assignedTo;
    }
}
