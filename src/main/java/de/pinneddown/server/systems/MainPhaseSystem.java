package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.EndMainPhaseAction;
import de.pinneddown.server.actions.PlayStarshipAction;
import de.pinneddown.server.components.AssignmentComponent;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.util.PlayerUtils;
import org.springframework.stereotype.Component;

@Component
public class MainPhaseSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private PlayerReadyManager playerReadyManager;
    private PlayerUtils playerUtils;

    public MainPhaseSystem(EventManager eventManager, EntityManager entityManager,
                           PlayerReadyManager playerReadyManager, PlayerUtils playerUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.playerReadyManager = playerReadyManager;
        this.playerUtils = playerUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(ActionType.END_MAIN_PHASE, this::onEndMainPhase);
        this.eventManager.addEventHandler(ActionType.PLAY_STARSHIP, this::onPlayStarship);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.MAIN));
    }

    private void onEndMainPhase(GameEvent gameEvent) {
        EndMainPhaseAction eventData = (EndMainPhaseAction)gameEvent.getEventData();
        playerReadyManager.setPlayerReady(eventData.getPlayerId());

        if (!playerReadyManager.allPlayersAreReady()) {
            return;
        }

        playerReadyManager.resetReadyPlayers();

        this.eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.ATTACK));
    }

    private void onPlayStarship(GameEvent gameEvent) {
        PlayStarshipAction eventData = (PlayStarshipAction)gameEvent.getEventData();

        // Get player.
        long playerEntityId = playerUtils.getPlayerEntityId(eventData.getPlayerId());

        if (playerEntityId == EntityManager.INVALID_ENTITY) {
            return;
        }

        // Play card.
        long entityId = playerUtils.playCard(playerEntityId, eventData.getBlueprintId());

        // Add additional components.
        OwnerComponent ownerComponent = new OwnerComponent();
        ownerComponent.setOwner(playerEntityId);
        entityManager.addComponent(entityId, ownerComponent);
        entityManager.addComponent(entityId, new AssignmentComponent());
    }
}
