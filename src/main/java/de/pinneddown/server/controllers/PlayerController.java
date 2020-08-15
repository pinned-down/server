package de.pinneddown.server.controllers;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.JoinGameAction;
import de.pinneddown.server.events.PlayerJoinedEvent;
import de.pinneddown.server.events.ReadyToStartEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Controller
public class PlayerController {
    @Autowired
    private MatchmakingService matchmakingService;

    @Autowired
    private PlayerManager playerManager;

    @Autowired
    private EventManager eventManager;

    @MessageMapping("/join")
    @SendTo("/topic/events")
    public WebSocketMessage join(JoinGameAction message) {
        // Verify player.
        matchmakingService.notifyPlayerJoined(message.getPlayerId());

        // Add player.
        playerManager.addPlayer(message.getPlayerId());

        // Check player count.
        ArrayList<String> playerIds = playerManager.getPlayerIds();

        if (playerIds.size() >= playerManager.getMaxPlayers()) {
            ReadyToStartEvent eventData = new ReadyToStartEvent(playerIds);
            eventManager.queueEvent(EventType.READY_TO_START, eventData);
        }

        return new WebSocketMessage(new PlayerJoinedEvent(message.getPlayerId()));
    }
}
