package de.pinneddown.server.events;

public class PlayerEntityCreatedEvent {
    private String playerId;
    private long entityId;

    public PlayerEntityCreatedEvent() {
    }

    public PlayerEntityCreatedEvent(String playerId, long entityId) {
        this.playerId = playerId;
        this.entityId = entityId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return "PlayerEntityCreatedEvent{" +
                "playerId='" + playerId + '\'' +
                ", entityId=" + entityId +
                '}';
    }
}
