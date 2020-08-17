package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class FlagshipSystem {
    private EventManager eventManager;
    private PlayerManager playerManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;

    public FlagshipSystem(EventManager eventManager, PlayerManager playerManager, EntityManager entityManager,
                          BlueprintManager blueprintManager) {
        this.eventManager = eventManager;
        this.playerManager = playerManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;

        this.eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::onPlayerEntityCreated);
    }

    private void onPlayerEntityCreated(GameEvent gameEvent) {
        PlayerEntityCreatedEvent eventData = (PlayerEntityCreatedEvent)gameEvent.getEventData();

        PlayerComponent playerComponent = entityManager.getComponent(eventData.getEntityId(), PlayerComponent.class);
        DeckList deckList = playerManager.getDeckList(playerComponent.getPlayerId());
        String flagshipBlueprintId = deckList.getFlagship();
        long entityId = blueprintManager.createEntity(flagshipBlueprintId);

        OwnerComponent ownerComponent = entityManager.getComponent(entityId, OwnerComponent.class);
        ownerComponent.setOwner(eventData.getEntityId());
    }
}
