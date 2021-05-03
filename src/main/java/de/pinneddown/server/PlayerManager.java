package de.pinneddown.server;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PlayerManager {
    private HashMap<String, Player> remoteAddressToPlayers;
    private int maxPlayers;

    public PlayerManager() {
        this.remoteAddressToPlayers = new HashMap<>();
        this.maxPlayers = 1;
    }

    public void addPlayer(String remoteAddress, String playerId, String providerUserId) {
        Player player = new Player();
        player.setRemoteAddress(remoteAddress);
        player.setPlayerId(playerId);
        player.setProviderUserId(providerUserId);
        this.remoteAddressToPlayers.put(remoteAddress, player);
    }

    public void removePlayer(String remoteAddress) {
        this.remoteAddressToPlayers.remove(remoteAddress);
    }

    public Collection<Player> getPlayers() {
        return remoteAddressToPlayers.values();
    }

    public Player getPlayerByRemoteAddress(String remoteAddress) {
        return remoteAddressToPlayers.getOrDefault(remoteAddress, null);
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
        cards.put("TRBTruth", 4);
        cards.put("TRBUnity", 4);

        DeckList deckList = new DeckList();
        deckList.setFlagship("TRBArdor");
        deckList.setCards(cards);

        return deckList;
    }
}
