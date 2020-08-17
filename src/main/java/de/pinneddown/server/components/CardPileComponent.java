package de.pinneddown.server.components;

import de.pinneddown.server.CardPile;
import de.pinneddown.server.EntityComponent;

public class CardPileComponent implements EntityComponent {
    private CardPile cardPile;

    public CardPile getCardPile() {
        return cardPile;
    }

    public void setCardPile(CardPile cardPile) {
        this.cardPile = cardPile;
    }
}
