package de.pinneddown.server.controllers;

import de.pinneddown.server.ActionType;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.PlayerManager;
import de.pinneddown.server.actions.GodCheatAction;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class CheatController extends WebSocketController {
    public CheatController(PlayerManager playerManager, EventManager eventManager) {
        super(playerManager, eventManager);
    }

    @MessageMapping("/godCheat")
    public void godCheat(SimpMessageHeaderAccessor headerAccessor, GodCheatAction message) {
        relayActionToGame(ActionType.GOD_CHEAT, message, headerAccessor);
    }
}
