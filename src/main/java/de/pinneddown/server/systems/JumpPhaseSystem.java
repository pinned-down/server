package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.BlueprintComponent;
import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.components.DistanceComponent;
import de.pinneddown.server.components.UpkeepComponent;
import de.pinneddown.server.events.*;
import de.pinneddown.server.util.ThreatUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@Component
public class JumpPhaseSystem {
    private static final int VICTORY_DISTANCE = 10;

    private EventManager eventManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;
    private Random random;
    private ThreatUtils threatUtils;

    private long locationDeckEntityId;
    private long currentLocationEntityId;

    private ArrayList<Long> upkeepEntities;

    public JumpPhaseSystem(EventManager eventManager, EntityManager entityManager, BlueprintManager blueprintManager,
                           Random random, ThreatUtils threatUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;
        this.random = random;
        this.threatUtils = threatUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);
        this.eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);
        this.eventManager.addEventHandler(EventType.CARD_REMOVED, this::onCardRemoved);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        upkeepEntities = new ArrayList<>();

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

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        if (eventData.getTurnPhase() != TurnPhase.JUMP) {
            return;
        }

        // Check victory condition.
        DistanceComponent totalDistanceComponent = entityManager.getComponent(locationDeckEntityId, DistanceComponent.class);

        if (totalDistanceComponent.getDistance() >= VICTORY_DISTANCE) {
            eventManager.queueEvent(EventType.VICTORY, new VictoryEvent());
            return;
        }

        // Reveal next location.
        revealNextLocation();

        // Add upkeep threat.
        for (long upkeepEntityId : upkeepEntities) {
            UpkeepComponent upkeepComponent = entityManager.getComponent(upkeepEntityId, UpkeepComponent.class);
            threatUtils.setThreat(threatUtils.getThreat() + upkeepComponent.getUpkeep());
        }

        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.MAIN));
    }

    private void onCardPlayed(GameEvent gameEvent) {
        CardPlayedEvent eventData = (CardPlayedEvent)gameEvent.getEventData();
        UpkeepComponent upkeepComponent = entityManager.getComponent(eventData.getEntityId(), UpkeepComponent.class);

        if (upkeepComponent != null) {
            upkeepEntities.add(eventData.getEntityId());
        }
    }

    private void onCardRemoved(GameEvent gameEvent) {
        CardRemovedEvent eventData = (CardRemovedEvent)gameEvent.getEventData();
        UpkeepComponent upkeepComponent = entityManager.getComponent(eventData.getEntityId(), UpkeepComponent.class);

        if (upkeepComponent != null) {
            upkeepEntities.remove(eventData.getEntityId());
        }
    }

    private void revealNextLocation() {
        CardPileComponent cardPileComponent = entityManager.getComponent(locationDeckEntityId, CardPileComponent.class);

        // Discard old location.
        BlueprintComponent blueprintComponent = entityManager.getComponent(currentLocationEntityId, BlueprintComponent.class);

        if (blueprintComponent != null) {
            cardPileComponent.getDiscardPile().push(blueprintComponent.getBlueprintId());

            entityManager.removeEntity(currentLocationEntityId);

            eventManager.queueEvent(EventType.CARD_REMOVED, new CardRemovedEvent(currentLocationEntityId));
        }

        // Reveal new location.
        String topLocation = cardPileComponent.getCardPile().pop();
        currentLocationEntityId = blueprintManager.createEntity(topLocation);

        CurrentLocationChangedEvent currentLocationChangedEvent =
                new CurrentLocationChangedEvent(currentLocationEntityId, topLocation);
        eventManager.queueEvent(EventType.CURRENT_LOCATION_CHANGED, currentLocationChangedEvent);

        eventManager.queueEvent(EventType.CARD_PLAYED, new CardPlayedEvent(currentLocationEntityId, topLocation, 0L));

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

        cards.put("Agniar", 1);
        cards.put("BerenaPulsar", 1);
        cards.put("GerraraNebula", 1);
        cards.put("Harana", 1);
        cards.put("KressarShipyards", 1);
        cards.put("MoslovAsteroid", 1);
        cards.put("OshiroAsteroidBelt", 1);
        cards.put("RahraAsteroidBelt", 1);
        cards.put("SalazaDocks", 1);
        cards.put("SolmarBorderStation", 1);
        cards.put("TekanaPassage", 1);
        cards.put("VolarPulsar", 1);

        deckList.setCards(cards);
        return deckList;
    }
}
