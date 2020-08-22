package de.pinneddown.server;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

        DeckList deckList = new DeckList();
        deckList.setFlagship("TRBArdor");
        deckList.setCards(cards);

        return deckList;
    }
}
