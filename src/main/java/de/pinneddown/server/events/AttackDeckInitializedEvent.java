package de.pinneddown.server.events;

public class AttackDeckInitializedEvent {
    private long entityId;

    public AttackDeckInitializedEvent() {
    }

    public AttackDeckInitializedEvent(long entityId) {
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
        return "AttackDeckInitializedEvent{" +
                "entityId=" + entityId +
                '}';
    }
}
