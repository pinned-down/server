package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.PlayEffectAction;
import de.pinneddown.server.events.CardRemovedEvent;
import de.pinneddown.server.util.PlayerUtils;
import org.springframework.stereotype.Component;

@Component
public class PlayEffectSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private PlayerUtils playerUtils;

    public PlayEffectSystem(EventManager eventManager, EntityManager entityManager, PlayerUtils playerUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.playerUtils = playerUtils;

        this.eventManager.addEventHandler(ActionType.PLAY_EFFECT, this::onPlayEffect);
    }

    private void onPlayEffect(GameEvent gameEvent) {
        PlayEffectAction eventData = (PlayEffectAction)gameEvent.getEventData();

        // Get player.
        long playerEntityId = playerUtils.getPlayerEntityId(eventData.getPlayerId());

        if (playerEntityId == EntityManager.INVALID_ENTITY) {
            return;
        }

        // Play card (this will automatically activate all abilities).
        long entityId = playerUtils.playCard(playerEntityId, eventData.getBlueprintId(), eventData.getTargetEntityId());

        // Remove card again.
        entityManager.removeEntity(entityId);
        eventManager.queueEvent(EventType.CARD_REMOVED, new CardRemovedEvent(entityId));

        playerUtils.addCardToDiscardPile(playerEntityId, eventData.getBlueprintId());
    }
}
