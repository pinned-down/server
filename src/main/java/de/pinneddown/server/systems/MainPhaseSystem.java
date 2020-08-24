package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.EndMainPhaseAction;

public class MainPhaseSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private PlayerReadyManager playerReadyManager;

    public MainPhaseSystem(EventManager eventManager, EntityManager entityManager, PlayerReadyManager playerReadyManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.playerReadyManager = playerReadyManager;

        this.eventManager.addEventHandler(ActionType.END_MAIN_PHASE, this::onEndMainPhase);
    }

    private void onEndMainPhase(GameEvent gameEvent) {
        EndMainPhaseAction eventData = (EndMainPhaseAction)gameEvent.getEventData();
        playerReadyManager.setPlayerReady(eventData.getPlayerId());

        if (!playerReadyManager.allPlayersAreReady()) {
            return;
        }

        playerReadyManager.resetReadyPlayers();

        this.eventManager.queueEvent(EventType.MAIN_PHASE_ENDED, null);
    }
}
