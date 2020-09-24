package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class CardDrawComponent implements EntityComponent {
    private int cards;

    public int getCards() {
        return cards;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }
}
