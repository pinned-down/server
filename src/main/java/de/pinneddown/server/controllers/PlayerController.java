package de.pinneddown.server.controllers;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.JoinGameAction;
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
    private MatchmakingService matchmakingService;
    private SimpMessagingTemplate template;

    private Logger logger = LoggerFactory.getLogger(ServerApplication.class);

    public PlayerController(PlayerManager playerManager, EventManager eventManager,
                            MatchmakingService matchmakingService, SimpMessagingTemplate template) {
        super(playerManager, eventManager);

        this.matchmakingService = matchmakingService;
        this.template = template;

        eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::sendEventToClients);
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

    private void sendEventToClients(GameEvent gameEvent) {
        WebSocketMessage message = new WebSocketMessage(gameEvent.getEventData());
        this.template.convertAndSend("/topic/events", message);
    }
}
