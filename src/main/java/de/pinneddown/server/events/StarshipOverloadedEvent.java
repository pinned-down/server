package de.pinneddown.server.events;

public class StarshipOverloadedEvent {
    private long entityId;

    public StarshipOverloadedEvent() {
    }

    public StarshipOverloadedEvent(long entityId) {
        this.entityId = entityId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }
}
