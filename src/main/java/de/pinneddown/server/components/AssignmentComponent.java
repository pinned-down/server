package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class AssignmentComponent implements EntityComponent {
    private long assignedTo;

    public long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(long assignedTo) {
        this.assignedTo = assignedTo;
    }
}
