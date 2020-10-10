package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.*;
import de.pinneddown.server.util.ThreatUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@Component
public class AttackPhaseSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;
    private Random random;
    private ThreatUtils threatUtils;

    private long attackDeckEntityId;
    private int totalDistance;
    private ArrayList<Long> playerEntities;
    private ArrayList<Long> playerStarships;
    private ArrayList<Long> enemyStarships;

    public AttackPhaseSystem(EventManager eventManager, EntityManager entityManager,
                             BlueprintManager blueprintManager, Random random, ThreatUtils threatUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;
        this.random = random;
        this.threatUtils = threatUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::onPlayerEntityCreated);
        this.eventManager.addEventHandler(EventType.TOTAL_DISTANCE_CHANGED, this::onTotalDistanceChanged);
        this.eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);
        this.eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        playerEntities = new ArrayList<>();
        playerStarships = new ArrayList<>();
        enemyStarships = new ArrayList<>();

        DeckList deckList = getDeckList();
        CardPile attackDeck = CardPile.createFromDecklist(deckList, random);

        attackDeckEntityId = entityManager.createEntity();

        CardPileComponent cardPileComponent = new CardPileComponent();
        cardPileComponent.setCardPile(attackDeck);

        entityManager.addComponent(attackDeckEntityId, cardPileComponent);

        // Notify listeners.
        AttackDeckInitializedEvent attackDeckInitializedEvent = new AttackDeckInitializedEvent(attackDeckEntityId);
        eventManager.queueEvent(EventType.ATTACK_DECK_INITIALIZED, attackDeckInitializedEvent);
    }

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        switch (eventData.getTurnPhase()) {
            case ATTACK:
                onAttackPhaseStarted();
                break;

            case JUMP:
                onJumpPhaseStarted();
                break;
        }
    }

    private void onAttackPhaseStarted() {
        int newThreat = threatUtils.getThreat();

        // Add threat for locations.
        newThreat += totalDistance;
        threatUtils.setThreat(newThreat, ThreatChangeReason.TOTAL_DISTANCE, EntityManager.INVALID_ENTITY);

        // Add threat for player starships.
        newThreat += playerStarships.size();
        threatUtils.setThreat(newThreat, ThreatChangeReason.FLEET_SIZE, EntityManager.INVALID_ENTITY);

        // Play attack cards.
        CardPileComponent attackDeck = entityManager.getComponent(attackDeckEntityId, CardPileComponent.class);

        while (!(attackDeck.getCardPile().isEmpty() && attackDeck.getDiscardPile().isEmpty())) {
            // Check if any cards left.
            if (attackDeck.getCardPile().isEmpty()) {
                // Shuffle discard pile into deck.
                attackDeck.getDiscardPile().shuffleInto(attackDeck.getCardPile(), random);
            }

            // Check top-most card.
            String cardBlueprintId = attackDeck.getCardPile().pop();
            long entityId = blueprintManager.createEntity(cardBlueprintId);
            enemyStarships.add(entityId);

            int cardThreat = threatUtils.getThreat(entityId);

            if (cardThreat > newThreat) {
                // Threat exhausted. Discard excess card.
                attackDeck.getDiscardPile().push(cardBlueprintId);
                break;
            }

            // Play card.
            CardPlayedEvent cardPlayedEventData = new CardPlayedEvent(entityId, cardBlueprintId, 0L);
            eventManager.queueEvent(EventType.CARD_PLAYED, cardPlayedEventData);

            newThreat -= cardThreat;

            // Set resulting threat.
            threatUtils.setThreat(newThreat, ThreatChangeReason.ENEMY_CARD_PLAYED, entityId);
        }

        // Enter next phase.
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.ASSIGNMENT));
    }

    private void onJumpPhaseStarted() {
        // Discard enemy starships and add threat.
        CardPileComponent attackDeck = entityManager.getComponent(attackDeckEntityId, CardPileComponent.class);

        int newThreat = threatUtils.getThreat();

        for (Long entityId : enemyStarships) {
            BlueprintComponent blueprintComponent = entityManager.getComponent(entityId, BlueprintComponent.class);

            if (blueprintComponent != null) {
                // Entity is still alive.
                attackDeck.getDiscardPile().push(blueprintComponent.getBlueprintId());

                eventManager.queueEvent(EventType.CARD_REMOVED, new CardRemovedEvent(entityId));
                entityManager.removeEntity(entityId);

                ++newThreat;

                threatUtils.setThreat(newThreat, ThreatChangeReason.ENEMY_CARD_DISCARDED, entityId);
            }
        }

        enemyStarships.clear();
    }

    private void onCardPlayed(GameEvent gameEvent) {
        CardPlayedEvent eventData = (CardPlayedEvent)gameEvent.getEventData();

        // Check if starship.
        long entityId = eventData.getEntityId();
        GameplayTagsComponent gameplayTagsComponent =
                entityManager.getComponent(entityId, GameplayTagsComponent.class);

        if (gameplayTagsComponent == null ||
                !gameplayTagsComponent.getInitialGameplayTags().contains(GameplayTags.CARDTYPE_STARSHIP)) {
            return;
        }

        // Check if owned by player.
        OwnerComponent ownerComponent = entityManager.getComponent(entityId, OwnerComponent.class);

        if (ownerComponent == null || !playerEntities.contains(ownerComponent.getOwner())) {
            return;
        }

        playerStarships.add(entityId);
    }

    private void onPlayerEntityCreated(GameEvent gameEvent) {
        PlayerEntityCreatedEvent eventData = (PlayerEntityCreatedEvent)gameEvent.getEventData();
        playerEntities.add(eventData.getEntityId());
    }

    private void onTotalDistanceChanged(GameEvent gameEvent) {
        TotalDistanceChangedEvent eventData = (TotalDistanceChangedEvent)gameEvent.getEventData();
        totalDistance = eventData.getTotalDistance();
    }

    private DeckList getDeckList() {
        DeckList deckList = new DeckList();
        HashMap<String, Integer> cards = new HashMap<>();

        cards.put("DratarAssaultFrigate", 2);
        cards.put("DBAgony", 1);

        deckList.setCards(cards);
        return deckList;
    }
}
