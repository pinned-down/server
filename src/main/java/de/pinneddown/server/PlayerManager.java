package de.pinneddown.server;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PlayerManager {
    private HashMap<String, String> remoteAddressToPlayerIds;
    private int maxPlayers;

    public PlayerManager() {
        this.remoteAddressToPlayerIds = new HashMap<>();
        this.maxPlayers = 1;
    }

    public void addPlayer(String remoteAddress, String playerId) {
        this.remoteAddressToPlayerIds.put(remoteAddress, playerId);
    }

    public void removePlayer(String remoteAddress) {
        this.remoteAddressToPlayerIds.remove(remoteAddress);
    }

    public Collection<String> getPlayerIds() {
        return remoteAddressToPlayerIds.values();
    }

    public String getPlayerIdFromRemoteAddress(String remoteAddress) {
        return remoteAddressToPlayerIds.getOrDefault(remoteAddress, null);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public DeckList getDeckList(String playerId) {
        HashMap<String, Integer> cards = new HashMap<>();
        cards.put("FireAtWill", 4);
        cards.put("Defiance", 4);
        cards.put("EvasionManeuver", 4);
        cards.put("RaiseTheStakes", 4);
        cards.put("TargetEliminated", 4);
        cards.put("TRBFaith", 4);
        cards.put("TRBJustice", 4);

        DeckList deckList = new DeckList();
        deckList.setFlagship("TRBArdor");
        deckList.setCards(cards);

        return deckList;
    }
}
