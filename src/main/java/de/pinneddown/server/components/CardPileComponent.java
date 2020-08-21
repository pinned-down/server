package de.pinneddown.server.components;

import de.pinneddown.server.CardPile;
import de.pinneddown.server.EntityComponent;

public class CardPileComponent implements EntityComponent {
    private CardPile cardPile;
    private CardPile discardPile;

    public CardPileComponent() {
        this.cardPile = new CardPile();
        this.discardPile = new CardPile();
    }

    public CardPile getCardPile() {
        return cardPile;
    }

    public void setCardPile(CardPile cardPile) {
        this.cardPile = cardPile;
    }

    public CardPile getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(CardPile discardPile) {
        this.discardPile = discardPile;
    }
}
