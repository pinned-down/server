package de.pinneddown.server.messages;

public class JoinedMessage {
    private String playerId;

    public JoinedMessage() {
    }

    public JoinedMessage(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
