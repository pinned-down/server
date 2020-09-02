package de.pinneddown.server.controllers;

import de.pinneddown.server.ActionType;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.PlayerManager;
import de.pinneddown.server.actions.AssignStarshipAction;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class AssignmentPhaseController extends WebSocketController {
    public AssignmentPhaseController(PlayerManager playerManager, EventManager eventManager) {
        super(playerManager, eventManager);
    }

    @MessageMapping("/assignStarship")
    public void assignStarship(SimpMessageHeaderAccessor headerAccessor, AssignStarshipAction message) {
        relayActionToGame(ActionType.ASSIGN_STARSHIP, message, headerAccessor);
    }
}
