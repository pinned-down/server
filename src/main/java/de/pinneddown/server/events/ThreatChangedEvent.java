package de.pinneddown.server.events;

public class ThreatChangedEvent {
    private int newThreat;

    public ThreatChangedEvent() {
    }

    public ThreatChangedEvent(int newThreat) {
        this.newThreat = newThreat;
    }

    public int getNewThreat() {
        return newThreat;
    }

    public void setNewThreat(int newThreat) {
        this.newThreat = newThreat;
    }
}
