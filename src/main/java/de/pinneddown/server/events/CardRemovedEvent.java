package de.pinneddown.server.events;

public class CardRemovedEvent {
    private long entityId;

    public CardRemovedEvent() {
    }

    public CardRemovedEvent(long entityId) {
        this.entityId = entityId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return "CardRemovedEvent{" +
                "entityId=" + entityId +
                '}';
    }
}
