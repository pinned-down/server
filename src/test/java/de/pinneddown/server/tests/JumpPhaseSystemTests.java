package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.components.DistanceComponent;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.components.UpkeepComponent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.events.ThreatPoolInitializedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.systems.JumpPhaseSystem;
import de.pinneddown.server.util.ThreatUtils;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class JumpPhaseSystemTests {
    private boolean victory;

    @Test
    void createsLocationDeckAtStartOfGame() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        JumpPhaseSystem system = createSystem(eventManager, entityManager, 1);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        CardPileComponent cardPileComponent = entityManager.getComponent(1L, CardPileComponent.class);

        assertThat(cardPileComponent).isNotNull();
        assertThat(cardPileComponent.getCardPile()).isNotNull();
        assertThat(cardPileComponent.getCardPile().getCards()).isNotEmpty();
    }

    @Test
    void revealsInitialLocationAtStartOfGame() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        JumpPhaseSystem system = createSystem(eventManager, entityManager, 1);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        DistanceComponent distanceComponent = entityManager.getComponent(1L, DistanceComponent.class);

        assertThat(distanceComponent).isNotNull();
        assertThat(distanceComponent.getDistance()).isGreaterThan(0);
    }

    @Test
    void revealsNewLocationInJumpPhase() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        JumpPhaseSystem system = createSystem(eventManager, entityManager, 1);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        DistanceComponent distanceComponent = entityManager.getComponent(1L, DistanceComponent.class);
        int initialDistance = distanceComponent.getDistance();

        // ACT
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.JUMP));

        // ASSERT
        assertThat(distanceComponent.getDistance()).isGreaterThan(initialDistance);
    }

    @Test
    void victoryAtFinalLocation() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        JumpPhaseSystem system = createSystem(eventManager, entityManager, 10);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        victory = false;
        eventManager.addEventHandler(EventType.VICTORY, this::onVictory);

        // ACT
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.JUMP));

        // ASSERT
        assertThat(victory).isTrue();
    }

    @Test
    void addUpkeepThreat() {
        // ARRANGE
        // Setup system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        JumpPhaseSystem system = createSystem(eventManager, entityManager, 1);

        ThreatUtils threatUtils = new ThreatUtils(eventManager, entityManager);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // Add threat pool.
        long threatPoolEntityId = entityManager.createEntity();
        ThreatComponent threatComponent = new ThreatComponent();
        entityManager.addComponent(threatPoolEntityId, threatComponent);

        eventManager.queueEvent(EventType.THREAT_POOL_INITIALIZED, new ThreatPoolInitializedEvent(threatPoolEntityId));

        // Add upkeep entity.
        long upkeepEntityId = entityManager.createEntity();
        UpkeepComponent upkeepComponent = new UpkeepComponent();
        upkeepComponent.setUpkeep(1);
        entityManager.addComponent(upkeepEntityId, upkeepComponent);

        eventManager.queueEvent(EventType.CARD_PLAYED, new CardPlayedEvent(upkeepEntityId, null, 0L));
        int oldThreat = threatUtils.getThreat();

        // ACT
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.JUMP));

        // ASSERT
        assertThat(threatUtils.getThreat()).isEqualTo(oldThreat + upkeepComponent.getUpkeep());
    }

    private void onVictory(GameEvent gameEvent) {
        victory = true;
    }

    private JumpPhaseSystem createSystem(EventManager eventManager, EntityManager entityManager, int locationDistance) {
        GameSystemTestUtils testUtils = new GameSystemTestUtils();

        Blueprint locationBlueprint = new Blueprint();
        locationBlueprint.getComponents().add(DistanceComponent.class.getSimpleName());
        locationBlueprint.getAttributes().put("Distance", locationDistance);

        BlueprintManager blueprintManager = testUtils.createBlueprintManager(entityManager, locationBlueprint);

        Random random = new Random();
        ThreatUtils threatUtils = new ThreatUtils(eventManager, entityManager);

        return new JumpPhaseSystem(eventManager, entityManager, blueprintManager, random, threatUtils);
    }
}
