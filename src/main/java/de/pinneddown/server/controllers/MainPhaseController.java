package de.pinneddown.server.controllers;

import de.pinneddown.server.ActionType;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.PlayerManager;
import de.pinneddown.server.actions.EndMainPhaseAction;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class MainPhaseController extends WebSocketController {
    public MainPhaseController(PlayerManager playerManager, EventManager eventManager) {
        super(playerManager, eventManager);
    }

    @MessageMapping("/endMainPhase")
    public void endMainPhase(SimpMessageHeaderAccessor headerAccessor, EndMainPhaseAction message) {
        relayActionToGame(ActionType.END_MAIN_PHASE, message, headerAccessor);
    }
}
