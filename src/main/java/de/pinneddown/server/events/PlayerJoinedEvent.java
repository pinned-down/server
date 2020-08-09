package de.pinneddown.server.events;

public class PlayerJoinedEvent {
    private String playerId;

    public PlayerJoinedEvent() {
    }

    public PlayerJoinedEvent(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
