package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.AssignmentComponent;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.CardPlayedEvent;
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

        // Play flagship.
        PlayerComponent playerComponent = entityManager.getComponent(eventData.getEntityId(), PlayerComponent.class);
        DeckList deckList = playerManager.getDeckList(playerComponent.getPlayerId());
        String flagshipBlueprintId = deckList.getFlagship();
        long entityId = blueprintManager.createEntity(flagshipBlueprintId);

        // Add additional components.
        OwnerComponent ownerComponent = new OwnerComponent();
        ownerComponent.setOwner(eventData.getEntityId());
        entityManager.addComponent(entityId, ownerComponent);
        entityManager.addComponent(entityId, new AssignmentComponent());

        // Notify listeners.
        CardPlayedEvent cardPlayedEventData =
                new CardPlayedEvent(entityId, flagshipBlueprintId, eventData.getEntityId());
        eventManager.queueEvent(EventType.CARD_PLAYED, cardPlayedEventData);
    }
}
