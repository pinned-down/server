package de.pinneddown.server.util;

import de.pinneddown.server.*;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.events.PlayerDiscardPileChangedEvent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import de.pinneddown.server.events.PlayerHandChangedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PlayerUtils {
    private EventManager eventManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;
    private ThreatUtils threatUtils;

    private HashMap<String, Long> playerEntities;

    public PlayerUtils(EventManager eventManager, EntityManager entityManager, BlueprintManager blueprintManager,
                       ThreatUtils threatUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;
        this.threatUtils = threatUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::onPlayerEntityCreated);
    }

    public long getPlayerEntityId(String playerId) {
        return playerEntities.getOrDefault(playerId, EntityManager.INVALID_ENTITY);
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

    public long playCard(long playerEntityId, String card) {
        // Remove card.
        if (!removeHandCard(playerEntityId, card)) {
            return EntityManager.INVALID_ENTITY;
        }

        // Play card.
        long entityId = blueprintManager.createEntity(card);

        // Increase threat.
        int threat = threatUtils.getThreat(entityId);
        threatUtils.addThreat(threat, ThreatChangeReason.PLAYER_CARD_PLAYED, entityId);

        // Notify listeners.
        CardPlayedEvent cardPlayedEventData = new CardPlayedEvent(entityId, card, playerEntityId);
        eventManager.queueEvent(EventType.CARD_PLAYED, cardPlayedEventData);

        return entityId;
    }

    private void onReadyToStart(GameEvent gameEvent) {
        this.playerEntities = new HashMap<>();
    }

    private void onPlayerEntityCreated(GameEvent gameEvent) {
        PlayerEntityCreatedEvent eventData = (PlayerEntityCreatedEvent)gameEvent.getEventData();
        playerEntities.put(eventData.getPlayerId(), eventData.getEntityId());
    }
}
