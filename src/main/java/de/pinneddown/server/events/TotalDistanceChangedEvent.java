package de.pinneddown.server.events;

public class TotalDistanceChangedEvent {
    private int totalDistance;

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    @Override
    public String toString() {
        return "TotalDistanceChangedEvent{" +
                "totalDistance=" + totalDistance +
                '}';
    }
}
