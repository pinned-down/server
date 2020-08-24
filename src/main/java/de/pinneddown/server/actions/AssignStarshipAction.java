package de.pinneddown.server.actions;

public class AssignStarshipAction extends PlayerAction {
    private long assignedStarship;
    private long assignedTo;

    public long getAssignedStarship() {
        return assignedStarship;
    }

    public void setAssignedStarship(long assignedStarship) {
        this.assignedStarship = assignedStarship;
    }

    public long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(long assignedTo) {
        this.assignedTo = assignedTo;
    }
}
