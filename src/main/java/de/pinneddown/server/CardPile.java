package de.pinneddown.server;

import java.util.*;

public class CardPile {
    private ArrayList<String> cards;

    public CardPile() {
        this.cards = new ArrayList<>();
    }

    public static CardPile createFromDecklist(DeckList deckList, Random shuffleRandom) {
        CardPile cardPile = new CardPile();
        HashMap<String, Integer> deckListCards = deckList.getCards();

        for (Map.Entry<String, Integer> card : deckListCards.entrySet()) {
            for (int i = 0; i < card.getValue(); ++i) {
                cardPile.cards.add(card.getKey());
            }
        }

        if (shuffleRandom != null) {
            Collections.shuffle(cardPile.cards, shuffleRandom);
        }

        return cardPile;
    }

    public ArrayList<String> getCards() {
        return cards;
    }
}
