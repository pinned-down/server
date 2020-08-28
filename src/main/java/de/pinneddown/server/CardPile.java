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

    public String pop() {
        return cards.remove(cards.size() - 1);
    }

    public void push(String card) {
        cards.add(card);
    }

    public boolean isEmpty() { return cards.isEmpty(); }

    public void shuffleInto(CardPile other, Random shuffleRandom) {
        other.cards.addAll(cards);
        cards.clear();
        Collections.shuffle(other.cards, shuffleRandom);
    }

    public int size() {
        return cards.size();
    }
}
