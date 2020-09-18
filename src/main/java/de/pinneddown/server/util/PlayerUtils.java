package de.pinneddown.server.util;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.PlayerDiscardPileChangedEvent;
import de.pinneddown.server.events.PlayerHandChangedEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerUtils {
    private EventManager eventManager;
    private EntityManager entityManager;

    public PlayerUtils(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
    }

    public void addHandCard(long playerEntityId, String card) {
        PlayerComponent playerComponent = entityManager.getComponent(playerEntityId, PlayerComponent.class);
        playerComponent.getHand().push(card);

        PlayerHandChangedEvent playerHandChangedEvent =
                new PlayerHandChangedEvent(playerEntityId, playerComponent.getHand().getCards());
        eventManager.queueEvent(EventType.PLAYER_HAND_CHANGED, playerHandChangedEvent);
    }

    public boolean removeHandCard(long playerEntityId, String card) {
        PlayerComponent playerComponent = entityManager.getComponent(playerEntityId, PlayerComponent.class);

        if (!playerComponent.getHand().remove(card)) {
            return false;
        }

        PlayerHandChangedEvent playerHandChangedEvent =
                new PlayerHandChangedEvent(playerEntityId, playerComponent.getHand().getCards());
        eventManager.queueEvent(EventType.PLAYER_HAND_CHANGED, playerHandChangedEvent);

        return true;
    }

    public void addCardToDiscardPile(long playerEntityId, String card) {
        PlayerComponent playerComponent = entityManager.getComponent(playerEntityId, PlayerComponent.class);
        playerComponent.getDiscardPile().push(card);

        PlayerDiscardPileChangedEvent playerDiscardPileChangedEvent =
                new PlayerDiscardPileChangedEvent(playerEntityId, playerComponent.getDiscardPile().getCards());
        eventManager.queueEvent(EventType.PLAYER_DISCARD_PILE_CHANGED, playerDiscardPileChangedEvent);
    }
}
