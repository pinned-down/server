package de.pinneddown.server.events;

public class PlayerDrawDeckSizeChangedEvent {
    private long playerEntityId;
    private int drawDeckSize;

    public PlayerDrawDeckSizeChangedEvent() {
    }

    public PlayerDrawDeckSizeChangedEvent(long playerEntityId, int drawDeckSize) {
        this.playerEntityId = playerEntityId;
        this.drawDeckSize = drawDeckSize;
    }

    public long getPlayerEntityId() {
        return playerEntityId;
    }

    public void setPlayerEntityId(long playerEntityId) {
        this.playerEntityId = playerEntityId;
    }

    public int getDrawDeckSize() {
        return drawDeckSize;
    }

    public void setDrawDeckSize(int drawDeckSize) {
        this.drawDeckSize = drawDeckSize;
    }
}
