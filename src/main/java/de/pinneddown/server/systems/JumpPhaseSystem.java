package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.CardPileComponent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

@Component
public class JumpPhaseSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private Random random;

    private long locationDeckEntityId;

    public JumpPhaseSystem(EventManager eventManager, EntityManager entityManager, Random random) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.random = random;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        DeckList deckList = getDeckList();
        CardPile attackDeck = CardPile.createFromDecklist(deckList, random);

        locationDeckEntityId = entityManager.createEntity();

        CardPileComponent cardPileComponent = new CardPileComponent();
        cardPileComponent.setCardPile(attackDeck);

        entityManager.addComponent(locationDeckEntityId, cardPileComponent);
    }

    private DeckList getDeckList() {
        DeckList deckList = new DeckList();
        HashMap<String, Integer> cards = new HashMap<>();

        cards.put("SolmarBorderStation", 1);

        deckList.setCards(cards);
        return deckList;
    }
}
