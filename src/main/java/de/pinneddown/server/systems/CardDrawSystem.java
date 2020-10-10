package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.CardDiscardComponent;
import de.pinneddown.server.components.CardDrawComponent;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.*;
import de.pinneddown.server.util.PlayerUtils;
import org.springframework.stereotype.Component;

import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Random;

@Component
public class CardDrawSystem {
    private static final int INITIAL_CARDS = 3;

    private EventManager eventManager;
    private EntityManager entityManager;
    private PlayerManager playerManager;
    private Random random;
    private PlayerUtils playerUtils;

    private ArrayList<Long> playerEntities;

    public CardDrawSystem(EventManager eventManager, EntityManager entityManager, PlayerManager playerManager,
                          Random random, PlayerUtils playerUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.playerManager = playerManager;
        this.random = random;
        this.playerUtils = playerUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::onPlayerEntityCreated);
        this.eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        this.playerEntities = new ArrayList<>();
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

    private void onAbilityEffectApplied(GameEvent gameEvent) {
        AbilityEffectAppliedEvent eventData = (AbilityEffectAppliedEvent)gameEvent.getEventData();

        OwnerComponent ownerComponent = entityManager.getComponent(eventData.getTargetEntityId(), OwnerComponent.class);

        if (ownerComponent == null) {
            return;
        }

        CardDrawComponent cardDrawComponent =
                entityManager.getComponent(eventData.getEffectEntityId(), CardDrawComponent.class);

        if (cardDrawComponent != null) {
            for (int cards = 0; cards < cardDrawComponent.getCards(); ++cards) {
                drawCard(ownerComponent.getOwner());
            }
        }

        CardDiscardComponent cardDiscardComponent =
                entityManager.getComponent(eventData.getEffectEntityId(), CardDiscardComponent.class);

        if (cardDiscardComponent != null) {
            for (int cards = 0; cards < cardDiscardComponent.getDiscardedRandomCards(); ++cards) {
                discardCard(ownerComponent.getOwner());
            }
        }
    }

    private void drawCard(long playerEntityId) {
        PlayerComponent playerComponent = entityManager.getComponent(playerEntityId, PlayerComponent.class);

        if (playerComponent.getDrawDeck().isEmpty()) {
            return;
        }

        String card = playerComponent.getDrawDeck().pop();
        playerUtils.addHandCard(playerEntityId, card);

        // Notify listeners.
        eventManager.queueEvent(EventType.PLAYER_DRAW_DECK_SIZE_CHANGED,
                new PlayerDrawDeckSizeChangedEvent(playerEntityId, playerComponent.getDrawDeck().size()));
    }

    private void discardCard(long playerEntityId) {
        PlayerComponent playerComponent = entityManager.getComponent(playerEntityId, PlayerComponent.class);

        if (playerComponent.getHand().isEmpty()) {
            return;
        }

        String card = playerComponent.getHand().getRandomCard(random);

        playerUtils.removeHandCard(playerEntityId, card);
        playerUtils.addCardToDiscardPile(playerEntityId, card);
    }
}
