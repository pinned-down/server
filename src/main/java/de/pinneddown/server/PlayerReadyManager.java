package de.pinneddown.server;

import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class PlayerReadyManager {
    private PlayerManager playerManager;
    private HashSet<String> readyPlayers;

    public PlayerReadyManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.readyPlayers = new HashSet<>();
    }

    public void setPlayerReady(String playerId) {
        readyPlayers.add(playerId);
    }

    public void resetReadyPlayers() {
        readyPlayers.clear();
    }

    public boolean allPlayersAreReady() {
        for (String playerId : playerManager.getPlayerIds()) {
            if (!readyPlayers.contains(playerId)) {
                return false;
            }
        }

        return true;
    }
}
