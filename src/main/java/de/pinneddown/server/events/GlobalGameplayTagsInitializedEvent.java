package de.pinneddown.server.events;

public class GlobalGameplayTagsInitializedEvent {
    private long entityId;

    public GlobalGameplayTagsInitializedEvent() {
    }

    public GlobalGameplayTagsInitializedEvent(long entityId) {
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
        return "GlobalGameplayTagsInitializedEvent{" +
                "entityId=" + entityId +
                '}';
    }
}
