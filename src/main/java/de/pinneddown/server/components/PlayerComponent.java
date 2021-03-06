package de.pinneddown.server.components;

import de.pinneddown.server.CardPile;
import de.pinneddown.server.EntityComponent;

public class PlayerComponent implements EntityComponent {
    private String playerId;
    private CardPile drawDeck;
    private CardPile hand;
    private CardPile discardPile;

    public PlayerComponent() {
        this.drawDeck = new CardPile();
        this.hand = new CardPile();
        this.discardPile = new CardPile();
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public CardPile getDrawDeck() {
        return drawDeck;
    }

    public void setDrawDeck(CardPile drawDeck) {
        this.drawDeck = drawDeck;
    }

    public CardPile getHand() {
        return hand;
    }

    public void setHand(CardPile hand) {
        this.hand = hand;
    }

    public CardPile getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(CardPile discardPile) {
        this.discardPile = discardPile;
    }
}
