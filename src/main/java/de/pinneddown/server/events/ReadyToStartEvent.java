package de.pinneddown.server.events;

import java.util.ArrayList;

public class ReadyToStartEvent {
    private ArrayList<String> players;

    public ReadyToStartEvent() {
    }

    public ReadyToStartEvent(ArrayList<String> players) {
        this.players = players;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<String> players) {
        this.players = players;
    }
}
