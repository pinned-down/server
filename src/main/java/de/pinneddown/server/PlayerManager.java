package de.pinneddown.server;

import java.util.ArrayList;

public class PlayerManager {
    private ArrayList<String> playerIds;

    public PlayerManager() {
        this.playerIds = new ArrayList<>();
    }

    public void addPlayer(String playerId) {
        this.playerIds.add(playerId);
    }

    public boolean removePlayer(String playerId) {
        return this.playerIds.remove(playerId);
    }
}
