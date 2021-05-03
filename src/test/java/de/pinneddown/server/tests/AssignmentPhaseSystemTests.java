package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.EndAssignmentPhaseAction;
import de.pinneddown.server.components.AssignmentComponent;
import de.pinneddown.server.components.GameplayTagsComponent;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.systems.AssignmentPhaseSystem;
import de.pinneddown.server.util.AssignmentUtils;
import de.pinneddown.server.util.PlayerUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class AssignmentPhaseSystemTests {
    private boolean assignmentPhaseEnded;

    @Test
    void assignmentPhaseEndsWhenAllPlayersAreReadyAndAssignmentsCorrect() {
        // ARRANGE
        // Set up system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PlayerManager playerManager = new PlayerManager();

        AssignmentPhaseSystem system = createSystem(eventManager, entityManager, playerManager);

        // Add single player.
        String playerId = "PlayerA";
        playerManager.addPlayer("", playerId, null);

        // Listen for resulting event.
        assignmentPhaseEnded = false;
        eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);

        // ACT
        EndAssignmentPhaseAction actionData = new EndAssignmentPhaseAction();
        actionData.setPlayerId(playerId);

        eventManager.queueEvent(ActionType.END_ASSIGNMENT_PHASE, actionData);

        // ASSERT
        assertThat(assignmentPhaseEnded).isTrue();
    }

    @Test
    void assignmentPhaseDoesntEndWithoutAssignments() {
        // ARRANGE
        // Set up system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PlayerManager playerManager = new PlayerManager();

        AssignmentPhaseSystem system = createSystem(eventManager, entityManager, playerManager);

        // Add single player.
        String playerId = "PlayerA";
        playerManager.addPlayer("", playerId, null);

        // Add unassigned starships.
        long playerStarship = createStarshipEntity(entityManager);
        entityManager.addComponent(playerStarship, new OwnerComponent());
        entityManager.addComponent(playerStarship, new AssignmentComponent());
        eventManager.queueEvent(EventType.CARD_PLAYED,
                new CardPlayedEvent(playerStarship, null, EntityManager.INVALID_ENTITY, EntityManager.INVALID_ENTITY));

        long enemyStarship = createStarshipEntity(entityManager);
        eventManager.queueEvent(EventType.CARD_PLAYED,
                new CardPlayedEvent(enemyStarship, null, EntityManager.INVALID_ENTITY, EntityManager.INVALID_ENTITY));

        // Listen for resulting event.
        assignmentPhaseEnded = false;
        eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);

        // ACT
        EndAssignmentPhaseAction actionData = new EndAssignmentPhaseAction();
        actionData.setPlayerId(playerId);

        eventManager.queueEvent(ActionType.END_ASSIGNMENT_PHASE, actionData);

        // ASSERT
        assertThat(assignmentPhaseEnded).isFalse();
    }

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        if (eventData.getTurnPhase() == TurnPhase.FIGHT) {
            assignmentPhaseEnded = true;
        }
    }

    private long createStarshipEntity(EntityManager entityManager) {
        ArrayList<String> gameplayTags = new ArrayList<>();
        gameplayTags.add(GameplayTags.CARDTYPE_STARSHIP);

        long starship = entityManager.createEntity();
        GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
        gameplayTagsComponent.setInitialGameplayTags(gameplayTags);
        entityManager.addComponent(starship, gameplayTagsComponent);

        return starship;
    }

    private AssignmentPhaseSystem createSystem(EventManager eventManager, EntityManager entityManager,
                                               PlayerManager playerManager) {
        PlayerReadyManager playerReadyManager = new PlayerReadyManager(playerManager);
        AssignmentUtils assignmentUtils = new AssignmentUtils(eventManager, entityManager);

        AssignmentPhaseSystem system = new AssignmentPhaseSystem(eventManager, entityManager, playerReadyManager, assignmentUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        return system;
    }
}
