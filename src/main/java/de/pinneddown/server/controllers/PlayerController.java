package de.pinneddown.server.controllers;

import de.opengamebackend.matchmaking.model.ServerStatus;
import de.pinneddown.server.*;
import de.pinneddown.server.actions.JoinGameAction;
import de.pinneddown.server.actions.LeaveGameAction;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.services.MatchmakingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.stream.Collectors;

@Controller
public class PlayerController extends WebSocketController {
    private EntityManager entityManager;
    private MatchmakingService matchmakingService;
    private SimpMessagingTemplate template;

    private Logger logger = LoggerFactory.getLogger(ServerApplication.class);

    public PlayerController(PlayerManager playerManager, EventManager eventManager, EntityManager entityManager,
                            MatchmakingService matchmakingService, SimpMessagingTemplate template) {
        super(playerManager, eventManager);

        this.entityManager = entityManager;
        this.matchmakingService = matchmakingService;
        this.template = template;

        eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.PLAYER_HAND_CHANGED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.CURRENT_LOCATION_CHANGED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.CARD_PLAYED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.THREAT_CHANGED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.TOTAL_DISTANCE_CHANGED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.STARSHIP_ASSIGNED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.STARSHIP_DAMAGED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.CARD_REMOVED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.VICTORY, this::sendEventToClients);
        eventManager.addEventHandler(EventType.DEFEAT, this::sendEventToClients);
        eventManager.addEventHandler(EventType.STARSHIP_POWER_CHANGED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.GLOBAL_GAMEPLAY_TAGS_CHANGED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.PLAYER_DRAW_DECK_SIZE_CHANGED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.PLAYER_DISCARD_PILE_CHANGED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.THREAT_MODIFIERS_CHANGED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.ERROR, this::sendEventToClients);
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_DEACTIVATED, this::sendEventToClients);
        eventManager.addEventHandler(EventType.BATTLE_DESTINY_DRAWN, this::sendEventToClients);
        eventManager.addEventHandler(EventType.GAMEPLAY_TAGS_CHANGED, this::sendEventToClients);
    }

    @MessageMapping("/join")
    public void join(SimpMessageHeaderAccessor headerAccessor, JoinGameAction message) {
        // Verify player.
        String playerId = matchmakingService.notifyPlayerJoined(message.getTicket());

        // Add player.
        String remoteAddress = getRemoteAddressFromSession(headerAccessor);
        playerManager.addPlayer(remoteAddress, playerId, message.getPlayerId());

        // Check player count.
        Collection<String> playerIds = playerManager.getPlayers().stream()
                .map(Player::getProviderUserId)
                .collect(Collectors.toList());

        if (playerIds.size() >= playerManager.getMaxPlayers()) {
            matchmakingService.setServerStatus(ServerStatus.CLOSED);

            ReadyToStartEvent eventData = new ReadyToStartEvent(playerIds);
            eventManager.queueEvent(EventType.READY_TO_START, eventData);
        }
    }

    @MessageMapping("/leave")
    public void leave(SimpMessageHeaderAccessor headerAccessor, LeaveGameAction message) {
        // Verify player.
        String remoteAddress = getRemoteAddressFromSession(headerAccessor);
        Player player = playerManager.getPlayerByRemoteAddress(remoteAddress);
        matchmakingService.notifyPlayerLeft(player.getPlayerId());

        // Remove player.
        playerManager.removePlayer(remoteAddress);

        // Check player count.
        if (playerManager.getPlayers().size() <= 0) {
            // Reset game.
            entityManager.clear();

            logger.info("Removed all entities.");

            matchmakingService.setServerStatus(ServerStatus.OPEN);
        }
    }

    private void sendEventToClients(GameEvent gameEvent) {
        WebSocketMessage message = new WebSocketMessage(gameEvent.getEventData());
        this.template.convertAndSend("/topic/events", message);
    }
}
