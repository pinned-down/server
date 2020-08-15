package de.pinneddown.server;

import java.util.HashMap;

public class DeckList {
    private String name;
    private String flagship;
    private HashMap<String, Integer> cards;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlagship() {
        return flagship;
    }

    public void setFlagship(String flagship) {
        this.flagship = flagship;
    }

    public HashMap<String, Integer> getCards() {
        return cards;
    }

    public void setCards(HashMap<String, Integer> cards) {
        this.cards = cards;
    }
}
