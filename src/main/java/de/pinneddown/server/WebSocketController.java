package de.pinneddown.server;

import de.pinneddown.server.messages.JoinMessage;
import de.pinneddown.server.messages.JoinedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @Autowired
    private PlayerManager playerManager;

    @MessageMapping("/join")
    @SendTo("/topic/messages")
    public WebSocketMessage join(JoinMessage message) {
        playerManager.addPlayer(message.getPlayerId());
        return new WebSocketMessage(new JoinedMessage(message.getPlayerId()));
    }
}
