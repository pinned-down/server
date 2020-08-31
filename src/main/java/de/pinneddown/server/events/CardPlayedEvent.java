package de.pinneddown.server.events;

public class CardPlayedEvent {
    private long entityId;
    private String blueprintId;
    private long ownerEntityId;

    public CardPlayedEvent() {
    }

    public CardPlayedEvent(long entityId, String blueprintId, long ownerEntityId) {
        this.entityId = entityId;
        this.blueprintId = blueprintId;
        this.ownerEntityId = ownerEntityId;
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

    public long getOwnerEntityId() {
        return ownerEntityId;
    }

    public void setOwnerEntityId(long ownerEntityId) {
        this.ownerEntityId = ownerEntityId;
    }
}
