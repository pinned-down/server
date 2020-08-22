package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.EndMainPhaseAction;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import de.pinneddown.server.systems.MainPhaseSystem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MainPhaseSystemTests {
    private boolean mainPhaseEnded;

    @Test
    void mainPhaseEndsWhenAllPlayersAreReady() {
        // ARRANGE
        // Set up system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager();

        MainPhaseSystem system = new MainPhaseSystem(eventManager, entityManager);

        // Add single player.
        String playerId = "PlayerA";
        PlayerComponent playerComponent = new PlayerComponent();
        playerComponent.setPlayerId(playerId);

        long playerEntityId = entityManager.createEntity();
        entityManager.addComponent(playerEntityId, playerComponent);

        PlayerEntityCreatedEvent eventData = new PlayerEntityCreatedEvent();
        eventData.setEntityId(playerEntityId);

        eventManager.queueEvent(EventType.PLAYER_ENTITY_CREATED, eventData);

        // Listen for resulting event.
        mainPhaseEnded = false;
        eventManager.addEventHandler(EventType.MAIN_PHASE_ENDED, this::onMainPhaseEnded);

        // ACT
        EndMainPhaseAction actionData = new EndMainPhaseAction();
        actionData.setPlayerId(playerId);

        eventManager.queueEvent(ActionType.END_MAIN_PHASE, actionData);

        // ASSERT
        assertThat(mainPhaseEnded).isTrue();
    }

    private void onMainPhaseEnded(GameEvent gameEvent) {
        mainPhaseEnded = true;
    }
}
