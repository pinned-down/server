package de.pinneddown.server.controllers;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.JoinGameAction;
import de.pinneddown.server.actions.LeaveGameAction;
import de.pinneddown.server.events.ReadyToStartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Collection;

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
    }

    @MessageMapping("/join")
    public void join(SimpMessageHeaderAccessor headerAccessor, JoinGameAction message) {
        // Verify player.
        matchmakingService.notifyPlayerJoined(message.getPlayerId());

        // Add player.
        String remoteAddress = getRemoteAddressFromSession(headerAccessor);
        playerManager.addPlayer(remoteAddress, message.getPlayerId());

        // Check player count.
        Collection<String> playerIds = playerManager.getPlayerIds();

        if (playerIds.size() >= playerManager.getMaxPlayers()) {
            ReadyToStartEvent eventData = new ReadyToStartEvent(playerIds);
            eventManager.queueEvent(EventType.READY_TO_START, eventData);
        }
    }

    @MessageMapping("/leave")
    public void leave(SimpMessageHeaderAccessor headerAccessor, LeaveGameAction message) {
        // Verify player.
        matchmakingService.notifyPlayerLeft(message.getPlayerId());

        // Remove player.
        String remoteAddress = getRemoteAddressFromSession(headerAccessor);
        playerManager.removePlayer(remoteAddress);

        // Check player count.
        Collection<String> playerIds = playerManager.getPlayerIds();

        if (playerIds.size() <= 0) {
            // Reset game.
            entityManager.clear();

            logger.info("Removed all entities.");
        }
    }

    private void sendEventToClients(GameEvent gameEvent) {
        WebSocketMessage message = new WebSocketMessage(gameEvent.getEventData());
        this.template.convertAndSend("/topic/events", message);
    }
}
