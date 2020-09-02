package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.AssignStarshipAction;
import de.pinneddown.server.actions.EndAssignmentPhaseAction;
import de.pinneddown.server.components.AssignmentComponent;
import de.pinneddown.server.components.GameplayTagsComponent;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.events.StarshipAssignedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;

import java.util.HashSet;

public class AssignmentPhaseSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private PlayerReadyManager playerReadyManager;

    private HashSet<Long> playerStarships;
    private HashSet<Long> enemyStarships;

    public AssignmentPhaseSystem(EventManager eventManager, EntityManager entityManager,  PlayerReadyManager playerReadyManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.playerReadyManager = playerReadyManager;

        playerStarships = new HashSet<>();
        enemyStarships = new HashSet<>();

        this.eventManager.addEventHandler(ActionType.ASSIGN_STARSHIP, this::onAssignStarship);
        this.eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);
        this.eventManager.addEventHandler(ActionType.END_ASSIGNMENT_PHASE, this::onEndAssignmentPhase);
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

        if (ownerComponent == null) {
            enemyStarships.add(entityId);
        } else {
            playerStarships.add(entityId);
        }
    }

    private void onEndAssignmentPhase(GameEvent gameEvent) {
        EndAssignmentPhaseAction eventData = (EndAssignmentPhaseAction)gameEvent.getEventData();
        playerReadyManager.setPlayerReady(eventData.getPlayerId());

        // Check if all players ready.
        if (!playerReadyManager.allPlayersAreReady()) {
            return;
        }

        // Check if all assignments have been made.
        int requiredAssignments = Math.min(playerStarships.size(), enemyStarships.size());
        int actualAssigments = 0;

        for (long playerStarship : playerStarships) {
            AssignmentComponent assignmentComponent =
                    entityManager.getComponent(playerStarship, AssignmentComponent.class);

            if (assignmentComponent.getAssignedTo() > 0) {
                ++actualAssigments;
            }
        }

        if (actualAssigments < requiredAssignments) {
            return;
        }

        // End assignment phase.
        playerReadyManager.resetReadyPlayers();

        this.eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.FIGHT));
    }

    private void onAssignStarship(GameEvent gameEvent) {
        AssignStarshipAction eventData = (AssignStarshipAction)gameEvent.getEventData();

        AssignmentComponent assignmentComponent = entityManager.getComponent(eventData.getAssignedStarship(),
                AssignmentComponent.class);
        assignmentComponent.setAssignedTo(eventData.getAssignedTo());

        // Notify listeners.
        StarshipAssignedEvent starshipAssignedEvent =
                new StarshipAssignedEvent(eventData.getAssignedStarship(), eventData.getAssignedTo());

        eventManager.queueEvent(EventType.STARSHIP_ASSIGNED, starshipAssignedEvent);
    }
}
