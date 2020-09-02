package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.EndMainPhaseAction;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import org.springframework.stereotype.Component;

@Component
public class MainPhaseSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private PlayerReadyManager playerReadyManager;

    public MainPhaseSystem(EventManager eventManager, EntityManager entityManager, PlayerReadyManager playerReadyManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.playerReadyManager = playerReadyManager;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(ActionType.END_MAIN_PHASE, this::onEndMainPhase);
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
}
