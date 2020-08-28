package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.components.DistanceComponent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.systems.JumpPhaseSystem;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class JumpPhaseSystemTests extends GameSystemTestSuite {
    private boolean victory;

    @Test
    void createsLocationDeckAtStartOfGame() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        JumpPhaseSystem system = createSystem(eventManager, entityManager, 1);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        CardPileComponent cardPileComponent = entityManager.getComponent(0, CardPileComponent.class);

        assertThat(cardPileComponent).isNotNull();
        assertThat(cardPileComponent.getCardPile()).isNotNull();
        assertThat(cardPileComponent.getCardPile().getCards()).isNotEmpty();
    }

    @Test
    void revealsInitialLocationAtStartOfGame() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        JumpPhaseSystem system = createSystem(eventManager, entityManager, 1);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        DistanceComponent distanceComponent = entityManager.getComponent(0, DistanceComponent.class);

        assertThat(distanceComponent).isNotNull();
        assertThat(distanceComponent.getDistance()).isGreaterThan(0);
    }

    @Test
    void revealsNewLocationInJumpPhase() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        JumpPhaseSystem system = createSystem(eventManager, entityManager, 1);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        DistanceComponent distanceComponent = entityManager.getComponent(0, DistanceComponent.class);
        int initialDistance = distanceComponent.getDistance();

        // ACT
        eventManager.queueEvent(EventType.FIGHT_PHASE_ENDED, null);

        // ASSERT
        assertThat(distanceComponent.getDistance()).isGreaterThan(initialDistance);
    }

    @Test
    void victoryAtFinalLocation() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        JumpPhaseSystem system = createSystem(eventManager, entityManager, 10);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        victory = false;
        eventManager.addEventHandler(EventType.VICTORY, this::onVictory);

        // ACT
        eventManager.queueEvent(EventType.FIGHT_PHASE_ENDED, null);

        // ASSERT
        assertThat(victory).isTrue();
    }

    private void onVictory(GameEvent gameEvent) {
        victory = true;
    }

    private JumpPhaseSystem createSystem(EventManager eventManager, EntityManager entityManager, int locationDistance) {
        Blueprint locationBlueprint = new Blueprint();
        locationBlueprint.getComponents().add(DistanceComponent.class.getSimpleName());
        locationBlueprint.getAttributes().put("Distance", locationDistance);

        BlueprintManager blueprintManager = createMockBlueprintManager(entityManager, locationBlueprint);

        Random random = new Random();

        return new JumpPhaseSystem(eventManager, entityManager, blueprintManager, random);
    }
}
