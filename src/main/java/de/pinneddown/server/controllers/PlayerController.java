package de.pinneddown.server.controllers;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.JoinGameAction;
import de.pinneddown.server.events.PlayerJoinedEvent;
import de.pinneddown.server.events.ReadyToStartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Collection;

@Controller
public class PlayerController extends WebSocketController {
    private MatchmakingService matchmakingService;

    private Logger logger = LoggerFactory.getLogger(ServerApplication.class);

    public PlayerController(PlayerManager playerManager, EventManager eventManager, MatchmakingService matchmakingService) {
        super(playerManager, eventManager);

        this.matchmakingService = matchmakingService;
    }

    @MessageMapping("/join")
    @SendTo("/topic/events")
    public WebSocketMessage join(SimpMessageHeaderAccessor headerAccessor, JoinGameAction message) {
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

        return new WebSocketMessage(new PlayerJoinedEvent(message.getPlayerId()));
    }
}
