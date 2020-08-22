package de.pinneddown.server.events;

import java.util.ArrayList;
import java.util.Collection;

public class ReadyToStartEvent {
    private Collection<String> players;

    public ReadyToStartEvent() {
    }

    public ReadyToStartEvent(Collection<String> players) {
        this.players = players;
    }

    public Collection<String> getPlayers() {
        return players;
    }

    public void setPlayers(Collection<String> players) {
        this.players = players;
    }
}
