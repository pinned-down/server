package de.pinneddown.server.events;

public class CardPlayedEvent {
    private long entityId;

    public CardPlayedEvent() {
    }

    public CardPlayedEvent(long entityId) {
        this.entityId = entityId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }
}
