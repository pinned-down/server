package de.pinneddown.server.events;

public class CurrentLocationChangedEvent {
    private long entityId;
    private String blueprintId;

    public CurrentLocationChangedEvent() {
    }

    public CurrentLocationChangedEvent(long entityId, String blueprintId) {
        this.entityId = entityId;
        this.blueprintId = blueprintId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getBlueprintId() {
        return blueprintId;
    }

    public void setBlueprintId(String blueprintId) {
        this.blueprintId = blueprintId;
    }

    @Override
    public String toString() {
        return "CurrentLocationChangedEvent{" +
                "entityId=" + entityId +
                ", blueprintId='" + blueprintId + '\'' +
                '}';
    }
}
