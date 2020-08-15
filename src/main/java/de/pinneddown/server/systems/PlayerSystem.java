package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import de.pinneddown.server.events.ReadyToStartEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerSystem {
    private EventManager eventManager;
    private EntityManager entityManager;

    public PlayerSystem(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        ReadyToStartEvent eventData = (ReadyToStartEvent)gameEvent.getEventData();

        // Create player entities.
        for (String playerId : eventData.getPlayers()) {
            long playerEntityId = entityManager.createEntity();

            PlayerComponent playerComponent = new PlayerComponent();
            playerComponent.setPlayerId(playerId);

            entityManager.addComponent(playerEntityId, playerComponent);

            // Notify listeners.
            PlayerEntityCreatedEvent playerEntityCreatedEventData = new PlayerEntityCreatedEvent();
            playerEntityCreatedEventData.setEntityId(playerEntityId);

            eventManager.queueEvent(EventType.PLAYER_ENTITY_CREATED, playerEntityCreatedEventData);
        }
    }
}
