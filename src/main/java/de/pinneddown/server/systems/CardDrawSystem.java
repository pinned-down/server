package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CardDrawSystem {
    private static final int INITIAL_CARDS = 3;

    private EventManager eventManager;
    private EntityManager entityManager;
    private PlayerManager playerManager;
    private Random random;

    public CardDrawSystem(EventManager eventManager, EntityManager entityManager, PlayerManager playerManager,
                          Random random) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.playerManager = playerManager;
        this.random = random;

        this.eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::onPlayerEntityCreated);
    }

    private void onPlayerEntityCreated(GameEvent gameEvent) {
        PlayerEntityCreatedEvent eventData = (PlayerEntityCreatedEvent)gameEvent.getEventData();

        // Setup draw deck.
        PlayerComponent playerComponent = entityManager.getComponent(eventData.getEntityId(), PlayerComponent.class);
        DeckList deckList = playerManager.getDeckList(playerComponent.getPlayerId());
        CardPile drawDeck = CardPile.createFromDecklist(deckList, random);

        playerComponent.setDrawDeck(drawDeck);

        // Draw initial cards.
        for (int i = 0; i < INITIAL_CARDS; ++i) {
            String card = playerComponent.getDrawDeck().pop();
            playerComponent.getHand().push(card);
        }
    }
}
