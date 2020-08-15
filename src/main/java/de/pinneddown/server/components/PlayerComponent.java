package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class PlayerComponent implements EntityComponent {
    private String playerId;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
