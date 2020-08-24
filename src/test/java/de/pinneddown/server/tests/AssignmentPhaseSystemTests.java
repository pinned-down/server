package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.EndAssignmentPhaseAction;
import de.pinneddown.server.components.AssignmentComponent;
import de.pinneddown.server.components.GameplayTagsComponent;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.systems.AssignmentPhaseSystem;
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
        EntityManager entityManager = new EntityManager();
        PlayerManager playerManager = new PlayerManager();
        PlayerReadyManager playerReadyManager = new PlayerReadyManager(playerManager);

        AssignmentPhaseSystem system = new AssignmentPhaseSystem(eventManager, entityManager, playerReadyManager);

        // Add single player.
        String playerId = "PlayerA";
        playerManager.addPlayer("", playerId);

        // Listen for resulting event.
        assignmentPhaseEnded = false;
        eventManager.addEventHandler(EventType.ASSIGNMENT_PHASE_ENDED, this::onAssignmentPhaseEnded);

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
        EntityManager entityManager = new EntityManager();
        PlayerManager playerManager = new PlayerManager();
        PlayerReadyManager playerReadyManager = new PlayerReadyManager(playerManager);

        AssignmentPhaseSystem system = new AssignmentPhaseSystem(eventManager, entityManager, playerReadyManager);

        // Add single player.
        String playerId = "PlayerA";
        playerManager.addPlayer("", playerId);

        // Add unassigned starships.
        long playerStarship = createStarshipEntity(entityManager);
        entityManager.addComponent(playerStarship, new OwnerComponent());
        entityManager.addComponent(playerStarship, new AssignmentComponent());
        eventManager.queueEvent(EventType.CARD_PLAYED, new CardPlayedEvent(playerStarship));

        long enemyStarship = createStarshipEntity(entityManager);
        eventManager.queueEvent(EventType.CARD_PLAYED, new CardPlayedEvent(enemyStarship));

        // Listen for resulting event.
        assignmentPhaseEnded = false;
        eventManager.addEventHandler(EventType.ASSIGNMENT_PHASE_ENDED, this::onAssignmentPhaseEnded);

        // ACT
        EndAssignmentPhaseAction actionData = new EndAssignmentPhaseAction();
        actionData.setPlayerId(playerId);

        eventManager.queueEvent(ActionType.END_ASSIGNMENT_PHASE, actionData);

        // ASSERT
        assertThat(assignmentPhaseEnded).isFalse();
    }

    private void onAssignmentPhaseEnded(GameEvent gameEvent) {
        assignmentPhaseEnded = true;
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
}