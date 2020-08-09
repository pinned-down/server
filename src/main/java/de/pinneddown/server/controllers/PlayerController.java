package de.pinneddown.server.controllers;

import de.pinneddown.server.MatchmakingService;
import de.pinneddown.server.PlayerManager;
import de.pinneddown.server.WebSocketMessage;
import de.pinneddown.server.actions.JoinGameAction;
import de.pinneddown.server.events.PlayerJoinedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class PlayerController {
    @Autowired
    private MatchmakingService matchmakingService;

    @Autowired
    private PlayerManager playerManager;

    @MessageMapping("/join")
    @SendTo("/topic/events")
    public WebSocketMessage join(JoinGameAction message) {
        // Verify player.
        matchmakingService.notifyPlayerJoined(message.getPlayerId());

        // Add player.
        playerManager.addPlayer(message.getPlayerId());
        return new WebSocketMessage(new PlayerJoinedEvent(message.getPlayerId()));
    }
}
