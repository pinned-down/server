package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import de.pinneddown.server.events.PlayerHandChangedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;

@Component
public class CardDrawSystem {
    private static final int INITIAL_CARDS = 3;

    private EventManager eventManager;
    private EntityManager entityManager;
    private PlayerManager playerManager;
    private Random random;

    private ArrayList<Long> playerEntities;

    public CardDrawSystem(EventManager eventManager, EntityManager entityManager, PlayerManager playerManager,
                          Random random) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.playerManager = playerManager;
        this.random = random;

        this.playerEntities = new ArrayList<>();

        this.eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::onPlayerEntityCreated);
        this.eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);
    }

    private void onPlayerEntityCreated(GameEvent gameEvent) {
        PlayerEntityCreatedEvent eventData = (PlayerEntityCreatedEvent)gameEvent.getEventData();
        long playerEntityId = eventData.getEntityId();

        playerEntities.add(playerEntityId);

        // Setup draw deck.
        PlayerComponent playerComponent = entityManager.getComponent(playerEntityId, PlayerComponent.class);
        DeckList deckList = playerManager.getDeckList(playerComponent.getPlayerId());
        CardPile drawDeck = CardPile.createFromDecklist(deckList, random);

        playerComponent.setDrawDeck(drawDeck);

        // Draw initial cards.
        for (int i = 0; i < INITIAL_CARDS; ++i) {
            drawCard(playerEntityId);
        }
    }

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        if (eventData.getTurnPhase() != TurnPhase.JUMP) {
            return;
        }

        // Draw card.
        for (long entityId : playerEntities) {
            drawCard(entityId);
        }
    }

    private void drawCard(long playerEntityId) {
        PlayerComponent playerComponent = entityManager.getComponent(playerEntityId, PlayerComponent.class);

        String card = playerComponent.getDrawDeck().pop();
        playerComponent.getHand().push(card);

        PlayerHandChangedEvent playerHandChangedEvent = new PlayerHandChangedEvent(playerEntityId, playerComponent.getHand().getCards());
        eventManager.queueEvent(EventType.PLAYER_HAND_CHANGED, playerHandChangedEvent);
    }
}
