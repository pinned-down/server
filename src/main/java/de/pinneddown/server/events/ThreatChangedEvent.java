package de.pinneddown.server.events;

import de.pinneddown.server.ThreatChangeReason;

public class ThreatChangedEvent {
    private int oldThreat;
    private int newThreat;
    private ThreatChangeReason reason;
    private String reasonEntityBlueprintId;

    public ThreatChangedEvent() {
    }

    public ThreatChangedEvent(int oldThreat, int newThreat, ThreatChangeReason reason, String reasonEntityBlueprintId) {
        this.oldThreat = oldThreat;
        this.newThreat = newThreat;
        this.reason = reason;
        this.reasonEntityBlueprintId = reasonEntityBlueprintId;
    }

    public int getOldThreat() {
        return oldThreat;
    }

    public void setOldThreat(int oldThreat) {
        this.oldThreat = oldThreat;
    }

    public int getNewThreat() {
        return newThreat;
    }

    public void setNewThreat(int newThreat) {
        this.newThreat = newThreat;
    }

    public ThreatChangeReason getReason() {
        return reason;
    }

    public void setReason(ThreatChangeReason reason) {
        this.reason = reason;
    }

    public String getReasonEntityBlueprintId() {
        return reasonEntityBlueprintId;
    }

    public void setReasonEntityBlueprintId(String reasonEntityBlueprintId) {
        this.reasonEntityBlueprintId = reasonEntityBlueprintId;
    }

    @Override
    public String toString() {
        return "ThreatChangedEvent{" +
                "oldThreat=" + oldThreat +
                ", newThreat=" + newThreat +
                ", reason=" + reason +
                ", reasonEntityBlueprintId='" + reasonEntityBlueprintId + '\'' +
                '}';
    }
}
