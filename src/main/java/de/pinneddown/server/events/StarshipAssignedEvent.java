package de.pinneddown.server.events;

public class StarshipAssignedEvent {
    private long assignedStarship;
    private long assignedTo;

    public StarshipAssignedEvent() {
    }

    public StarshipAssignedEvent(long assignedStarship, long assignedTo) {
        this.assignedStarship = assignedStarship;
        this.assignedTo = assignedTo;
    }

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

    @Override
    public String toString() {
        return "StarshipAssignedEvent{" +
                "assignedStarship=" + assignedStarship +
                ", assignedTo=" + assignedTo +
                '}';
    }
}
