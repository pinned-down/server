package de.pinneddown.server.events;

public class ThreatPoolInitializedEvent {
    private long entityId;

    public ThreatPoolInitializedEvent() {
    }

    public ThreatPoolInitializedEvent(long entityId) {
        this.entityId = entityId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }
}
