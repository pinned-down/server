package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.*;
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

    private long attackDeckEntityId;
    private long threatPoolEntityId;
    private int totalDistance;
    private ArrayList<Long> playerEntities;
    private ArrayList<Long> playerStarships;
    private ArrayList<Long> enemyStarships;

    public AttackPhaseSystem(EventManager eventManager, EntityManager entityManager,
                             BlueprintManager blueprintManager, Random random) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;
        this.random = random;

        playerEntities = new ArrayList<>();
        playerStarships = new ArrayList<>();
        enemyStarships = new ArrayList<>();

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.THREAT_POOL_INITIALIZED, this::onThreatPoolInitialized);
        this.eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::onPlayerEntityCreated);
        this.eventManager.addEventHandler(EventType.TOTAL_DISTANCE_CHANGED, this::onTotalDistanceChanged);
        this.eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);
        this.eventManager.addEventHandler(EventType.MAIN_PHASE_ENDED, this::onMainPhaseEnded);
        this.eventManager.addEventHandler(EventType.FIGHT_PHASE_ENDED, this::onFightPhaseEnded);
    }

    private void onThreatPoolInitialized(GameEvent gameEvent) {
        ThreatPoolInitializedEvent eventData = (ThreatPoolInitializedEvent)gameEvent.getEventData();
        threatPoolEntityId = eventData.getEntityId();
    }

    private void onMainPhaseEnded(GameEvent gameEvent) {
        ThreatComponent threatPoolThreatComponent = entityManager.getComponent(threatPoolEntityId, ThreatComponent.class);
        int currentThreat = threatPoolThreatComponent.getThreat();
        int newThreat = currentThreat;

        // Add threat for locations.
        newThreat += totalDistance;

        // Add threat for player starships.
        newThreat += playerStarships.size();

        // Play attack cards.
        CardPileComponent attackDeck = entityManager.getComponent(attackDeckEntityId, CardPileComponent.class);
        boolean threatExhausted = false;

        while (!(attackDeck.getCardPile().isEmpty() && attackDeck.getDiscardPile().isEmpty()) &&
                !threatExhausted) {
            // Check if any cards left.
            if (attackDeck.getCardPile().isEmpty()) {
                // Shuffle discard pile into deck.
                attackDeck.getDiscardPile().shuffleInto(attackDeck.getCardPile(), random);
            }

            // Check top-most card.
            String cardBlueprintId = attackDeck.getCardPile().pop();
            long entityId = blueprintManager.createEntity(cardBlueprintId);
            enemyStarships.add(entityId);

            ThreatComponent cardThreatComponent = entityManager.getComponent(entityId, ThreatComponent.class);

            if (cardThreatComponent.getThreat() > newThreat) {
                threatExhausted = true;

                // Discard excess card.
                attackDeck.getDiscardPile().push(cardBlueprintId);
                break;
            }

            // Play card.
            CardPlayedEvent cardPlayedEventData = new CardPlayedEvent();
            cardPlayedEventData.setEntityId(entityId);

            eventManager.queueEvent(EventType.CARD_PLAYED, cardPlayedEventData);

            newThreat -= cardThreatComponent.getThreat();
        }

        // Set resulting threat.
        threatPoolThreatComponent.setThreat(newThreat);

        // Enter next phase.
        eventManager.queueEvent(EventType.ATTACK_PHASE_ENDED, null);
    }

    private void onFightPhaseEnded(GameEvent gameEvent) {
        // Discard enemy starships and add threat.
        CardPileComponent attackDeck = entityManager.getComponent(attackDeckEntityId, CardPileComponent.class);

        ThreatComponent threatPoolThreatComponent = entityManager.getComponent(threatPoolEntityId, ThreatComponent.class);
        int newThreat = threatPoolThreatComponent.getThreat();

        for (Long entityId : enemyStarships) {
            BlueprintComponent blueprintComponent = entityManager.getComponent(entityId, BlueprintComponent.class);

            attackDeck.getDiscardPile().push(blueprintComponent.getBlueprintId());

            entityManager.removeEntity(entityId);

            ++newThreat;
        }

        threatPoolThreatComponent.setThreat(newThreat);

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

    private void onReadyToStart(GameEvent gameEvent) {
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

    private DeckList getDeckList() {
        DeckList deckList = new DeckList();
        HashMap<String, Integer> cards = new HashMap<>();

        cards.put("DratarAssaultFrigate", 2);

        deckList.setCards(cards);
        return deckList;
    }
}
