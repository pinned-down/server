package de.pinneddown.server;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PlayerManager {
    private ArrayList<String> playerIds;
    private int maxPlayers;

    public PlayerManager() {
        this.playerIds = new ArrayList<>();
        this.maxPlayers = 1;
    }

    public void addPlayer(String playerId) {
        this.playerIds.add(playerId);
    }

    public boolean removePlayer(String playerId) {
        return this.playerIds.remove(playerId);
    }

    public ArrayList<String> getPlayerIds() {
        return playerIds;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public DeckList getDeckList(String playerId) {
        DeckList deckList = new DeckList();
        deckList.setFlagship("TRBArdor");
        return deckList;
    }
}
