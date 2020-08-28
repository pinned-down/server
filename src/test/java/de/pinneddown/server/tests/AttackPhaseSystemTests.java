package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.components.GameplayTagsComponent;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.events.*;
import de.pinneddown.server.systems.AttackPhaseSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class AttackPhaseSystemTests extends GameSystemTestSuite {
    private ArrayList<Long> cardsPlayed;

    @BeforeEach
    void beforeEach() {
        cardsPlayed = new ArrayList<>();
    }

    @Test
    void createsAttackDeckAtStartOfGame() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();
        BlueprintManager blueprintManager = createMockBlueprintManager(entityManager, null);
        Random random = new Random();

        AttackPhaseSystem system = new AttackPhaseSystem(eventManager, entityManager, blueprintManager, random);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        CardPileComponent cardPileComponent = entityManager.getComponent(0, CardPileComponent.class);

        assertThat(cardPileComponent).isNotNull();
        assertThat(cardPileComponent.getCardPile()).isNotNull();
        assertThat(cardPileComponent.getCardPile().getCards()).isNotEmpty();
    }

    @Test
    void addsThreatForLocations() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();
        ThreatComponent threatComponent = setupSystem(entityManager, eventManager, 0);

        // Set current distance.
        int totalDistance = 5;
        TotalDistanceChangedEvent totalDistanceChangedEventData = new TotalDistanceChangedEvent();
        totalDistanceChangedEventData.setTotalDistance(totalDistance);

        eventManager.queueEvent(EventType.TOTAL_DISTANCE_CHANGED, totalDistanceChangedEventData);

        // ACT
        eventManager.queueEvent(EventType.MAIN_PHASE_ENDED, null);

        // ASSERT
        assertThat(threatComponent.getThreat()).isEqualTo(totalDistance);
    }

    @Test
    void addsThreatForPlayerStarships() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();
        ThreatComponent threatComponent = setupSystem(entityManager, eventManager, 0);

        // Add player.
        long playerEntityId = entityManager.createEntity();

        PlayerEntityCreatedEvent playerEntityCreatedEventData = new PlayerEntityCreatedEvent();
        playerEntityCreatedEventData.setEntityId(playerEntityId);

        eventManager.queueEvent(EventType.PLAYER_ENTITY_CREATED, playerEntityCreatedEventData);

        // Add player starship.
        long entityId = entityManager.createEntity();

        GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
        ArrayList<String> gameplayTags = new ArrayList<>();
        gameplayTags.add(GameplayTags.CARDTYPE_STARSHIP);
        gameplayTagsComponent.setInitialGameplayTags(gameplayTags);
        entityManager.addComponent(entityId, gameplayTagsComponent);

        OwnerComponent ownerComponent = new OwnerComponent();
        ownerComponent.setOwner(playerEntityId);
        entityManager.addComponent(entityId, ownerComponent);

        CardPlayedEvent cardPlayedEventData = new CardPlayedEvent();
        cardPlayedEventData.setEntityId(entityId);

        eventManager.queueEvent(EventType.CARD_PLAYED, cardPlayedEventData);

        // ACT
        eventManager.queueEvent(EventType.MAIN_PHASE_ENDED, null);

        // ASSERT
        assertThat(threatComponent.getThreat()).isEqualTo(1);
    }

    @Test
    void playAttackCardsForThreat() {
        // ARRANGE
        int availableThreat = 5;
        int enemyThreatCost = 2;

        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();
        ThreatComponent threatComponent = setupSystem(entityManager, eventManager, enemyThreatCost);
        threatComponent.setThreat(availableThreat);

        // Listen for events.
        eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);

        // ACT
        eventManager.queueEvent(EventType.MAIN_PHASE_ENDED, null);

        // ASSERT
        assertThat(cardsPlayed.size()).isEqualTo(availableThreat / enemyThreatCost);
    }

    @Test
    void discardsAllEnemiesInJumpPhase() {
        // ARRANGE
        int availableThreat = 5;
        int enemyThreatCost = 2;

        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();
        ThreatComponent threatComponent = setupSystem(entityManager, eventManager, enemyThreatCost);
        threatComponent.setThreat(availableThreat);

        // Listen for events.
        eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);

        // ACT
        eventManager.queueEvent(EventType.MAIN_PHASE_ENDED, null);
        eventManager.queueEvent(EventType.FIGHT_PHASE_ENDED, null);

        // ASSERT
        for (long entityId : cardsPlayed) {
            assertThat(entityManager.getComponent(entityId, ThreatComponent.class)).isNull();
        }
    }

    private void onCardPlayed(GameEvent gameEvent) {
        CardPlayedEvent eventData = (CardPlayedEvent)gameEvent.getEventData();
        cardsPlayed.add(eventData.getEntityId());
    }

    private ThreatComponent setupSystem(EntityManager entityManager, EventManager eventManager, int enemyThreatCost) {
        // Setup system.
        Blueprint enemyBlueprint = new Blueprint();
        enemyBlueprint.getComponents().add(ThreatComponent.class.getSimpleName());
        enemyBlueprint.getAttributes().put("Threat", enemyThreatCost);
        BlueprintManager blueprintManager = createMockBlueprintManager(entityManager, enemyBlueprint);

        Random random = new Random();

        AttackPhaseSystem system = new AttackPhaseSystem(eventManager, entityManager, blueprintManager, random);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        // Initialize threat pool.
        long threatPoolEntityId = entityManager.createEntity();
        ThreatComponent threatComponent = new ThreatComponent();
        entityManager.addComponent(threatPoolEntityId, threatComponent);

        ThreatPoolInitializedEvent eventData = new ThreatPoolInitializedEvent();
        eventData.setEntityId(threatPoolEntityId);
        eventManager.queueEvent(EventType.THREAT_POOL_INITIALIZED, eventData);

        return threatComponent;
    }
}
