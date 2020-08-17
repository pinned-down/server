package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.CardPileComponent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

@Component
public class AttackPhaseSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private Random random;

    private long attackDeckEntityId;

    public AttackPhaseSystem(EventManager eventManager, EntityManager entityManager, Random random) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.random = random;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        DeckList deckList = getDeckList();
        CardPile attackDeck = CardPile.createFromDecklist(deckList, random);

        attackDeckEntityId = entityManager.createEntity();

        CardPileComponent cardPileComponent = new CardPileComponent();
        cardPileComponent.setCardPile(attackDeck);

        entityManager.addComponent(attackDeckEntityId, cardPileComponent);
    }

    private DeckList getDeckList() {
        DeckList deckList = new DeckList();
        HashMap<String, Integer> cards = new HashMap<>();

        cards.put("DratarAssaultFrigate", 2);

        deckList.setCards(cards);
        return deckList;
    }
}
