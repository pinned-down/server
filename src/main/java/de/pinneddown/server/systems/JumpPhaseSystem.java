package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.BlueprintComponent;
import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.components.DistanceComponent;
import de.pinneddown.server.events.TotalDistanceChangedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

@Component
public class JumpPhaseSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;
    private Random random;

    private long locationDeckEntityId;
    private long currentLocationEntityId;

    public JumpPhaseSystem(EventManager eventManager, EntityManager entityManager, BlueprintManager blueprintManager,
                           Random random) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;
        this.random = random;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.FIGHT_PHASE_ENDED, this::onFightPhaseEnded);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        // Setup location deck.
        DeckList deckList = getDeckList();
        CardPile attackDeck = CardPile.createFromDecklist(deckList, random);

        locationDeckEntityId = entityManager.createEntity();

        CardPileComponent cardPileComponent = new CardPileComponent();
        cardPileComponent.setCardPile(attackDeck);

        entityManager.addComponent(locationDeckEntityId, cardPileComponent);
        entityManager.addComponent(locationDeckEntityId, new DistanceComponent());

        // Reveal initial location.
        revealNextLocation();
    }

    private void onFightPhaseEnded(GameEvent gameEvent) {
        revealNextLocation();

        eventManager.queueEvent(EventType.JUMP_PHASE_ENDED, null);
    }

    private void revealNextLocation() {
        CardPileComponent cardPileComponent = entityManager.getComponent(locationDeckEntityId, CardPileComponent.class);

        // Discard old location.
        BlueprintComponent blueprintComponent = entityManager.getComponent(currentLocationEntityId, BlueprintComponent.class);

        if (blueprintComponent != null) {
            cardPileComponent.getDiscardPile().push(blueprintComponent.getBlueprintId());

            entityManager.removeEntity(currentLocationEntityId);
        }

        // Reveal new location.
        String topLocation = cardPileComponent.getCardPile().pop();
        currentLocationEntityId = blueprintManager.createEntity(topLocation);

        // Update total distance.
        DistanceComponent totalDistanceComponent = entityManager.getComponent(locationDeckEntityId, DistanceComponent.class);
        DistanceComponent newDistanceComponent = entityManager.getComponent(currentLocationEntityId, DistanceComponent.class);

        if (newDistanceComponent != null) {
            int newTotalDistance = totalDistanceComponent.getDistance() + newDistanceComponent.getDistance();
            totalDistanceComponent.setDistance(newTotalDistance);

            TotalDistanceChangedEvent eventData = new TotalDistanceChangedEvent();
            eventData.setTotalDistance(newTotalDistance);

            eventManager.queueEvent(EventType.TOTAL_DISTANCE_CHANGED, eventData);
        }
    }

    private DeckList getDeckList() {
        DeckList deckList = new DeckList();
        HashMap<String, Integer> cards = new HashMap<>();

        cards.put("BerenaPulsar", 1);
        cards.put("SolmarBorderStation", 1);

        deckList.setCards(cards);
        return deckList;
    }
}
