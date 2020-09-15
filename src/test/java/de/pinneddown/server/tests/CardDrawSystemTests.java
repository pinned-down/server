package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.systems.CardDrawSystem;
import de.pinneddown.server.util.PlayerUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class CardDrawSystemTests {
    @Test
    void playersDrawInitialCards() throws IOException {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        CardDrawSystem system = createSystem(eventManager, entityManager);

        // ACT
        long playerEntityId = entityManager.createEntity();
        PlayerComponent playerComponent = new PlayerComponent();
        entityManager.addComponent(playerEntityId, playerComponent);

        PlayerEntityCreatedEvent eventData = new PlayerEntityCreatedEvent();
        eventData.setEntityId(playerEntityId);

        eventManager.queueEvent(EventType.PLAYER_ENTITY_CREATED, eventData);

        // ASSERT
        assertThat(playerComponent.getHand()).isNotNull();
        assertThat(playerComponent.getHand().getCards()).isNotNull();
        assertThat(playerComponent.getHand().getCards()).isNotEmpty();
    }

    @Test
    void playersDrawCardsInJumpPhase() throws IOException {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        CardDrawSystem system = createSystem(eventManager, entityManager);

        // Create player.
        long playerEntityId = entityManager.createEntity();
        PlayerComponent playerComponent = new PlayerComponent();
        entityManager.addComponent(playerEntityId, playerComponent);

        PlayerEntityCreatedEvent eventData = new PlayerEntityCreatedEvent();
        eventData.setEntityId(playerEntityId);

        eventManager.queueEvent(EventType.PLAYER_ENTITY_CREATED, eventData);

        int cardsInHand = playerComponent.getHand().size();

        // ACT
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.JUMP));

        // ASSERT
        assertThat(playerComponent.getHand().size()).isGreaterThan(cardsInHand);
    }

    private CardDrawSystem createSystem(EventManager eventManager, EntityManager entityManager) {
        PlayerManager playerManager = new PlayerManager();
        Random random = new Random();
        PlayerUtils playerUtils = new PlayerUtils(eventManager, entityManager);

        CardDrawSystem system = new CardDrawSystem(eventManager, entityManager, playerManager, random, playerUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        return system;
    }
}
