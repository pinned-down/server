package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.EndMainPhaseAction;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;

import java.util.ArrayList;
import java.util.HashSet;

public class MainPhaseSystem {
    private EventManager eventManager;
    private EntityManager entityManager;

    private ArrayList<Long> playerEntities;
    private HashSet<String> readyPlayers;

    public MainPhaseSystem(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        playerEntities = new ArrayList<>();
        readyPlayers = new HashSet<>();

        this.eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::onPlayerEntityCreated);
        this.eventManager.addEventHandler(ActionType.END_MAIN_PHASE, this::onEndMainPhase);
    }

    private void onEndMainPhase(GameEvent gameEvent) {
        EndMainPhaseAction eventData = (EndMainPhaseAction)gameEvent.getEventData();
        readyPlayers.add(eventData.getPlayerId());

        if (!allPlayersAreReady()) {
            return;
        }

        readyPlayers.clear();

        this.eventManager.queueEvent(EventType.MAIN_PHASE_ENDED, null);
    }

    private void onPlayerEntityCreated(GameEvent gameEvent) {
        PlayerEntityCreatedEvent eventData = (PlayerEntityCreatedEvent)gameEvent.getEventData();
        playerEntities.add(eventData.getEntityId());
    }

    private boolean allPlayersAreReady() {
        for (Long playerEntityId : playerEntities) {
            PlayerComponent playerComponent = entityManager.getComponent(playerEntityId, PlayerComponent.class);

            if (!readyPlayers.contains(playerComponent.getPlayerId())) {
                return false;
            }
        }

        return true;
    }
}
