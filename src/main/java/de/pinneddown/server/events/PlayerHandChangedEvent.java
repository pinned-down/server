package de.pinneddown.server.events;

import java.util.ArrayList;

public class PlayerHandChangedEvent {
    private long playerEntityId;
    private ArrayList<String> cards;

    public PlayerHandChangedEvent() {
    }

    public PlayerHandChangedEvent(long playerEntityId, ArrayList<String> cards) {
        this.playerEntityId = playerEntityId;
        this.cards = new ArrayList<>(cards);
    }

    public long getPlayerEntityId() {
        return playerEntityId;
    }

    public void setPlayerEntityId(long playerEntityId) {
        this.playerEntityId = playerEntityId;
    }

    public ArrayList<String> getCards() {
        return cards;
    }

    public void setCards(ArrayList<String> cards) {
        this.cards = new ArrayList<>(cards);
    }
}
