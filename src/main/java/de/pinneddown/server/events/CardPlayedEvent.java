package de.pinneddown.server.events;

import de.pinneddown.server.EntityManager;

public class CardPlayedEvent {
    private long entityId;
    private String blueprintId;
    private long ownerEntityId;
    private long targetEntityId;

    public CardPlayedEvent() {
    }

    public CardPlayedEvent(long entityId, String blueprintId, long ownerEntityId, long targetEntityId) {
        this.entityId = entityId;
        this.blueprintId = blueprintId;
        this.ownerEntityId = ownerEntityId;
        this.targetEntityId = targetEntityId;
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

    public long getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(long targetEntityId) {
        this.targetEntityId = targetEntityId;
    }
}
