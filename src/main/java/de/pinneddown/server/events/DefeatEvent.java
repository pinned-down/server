package de.pinneddown.server.events;

import de.pinneddown.server.DefeatReason;

public class DefeatEvent {
    private DefeatReason reason;
    private long entityId;

    public DefeatEvent() {
    }

    public DefeatEvent(DefeatReason reason, long entityId) {
        this.reason = reason;
        this.entityId = entityId;
    }

    public DefeatReason getReason() {
        return reason;
    }

    public void setReason(DefeatReason reason) {
        this.reason = reason;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return "DefeatEvent{" +
                "reason=" + reason +
                ", entityId=" + entityId +
                '}';
    }
}
