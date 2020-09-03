package de.pinneddown.server.controllers;

import de.pinneddown.server.ActionType;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.PlayerManager;
import de.pinneddown.server.actions.ResolveFightAction;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class FightPhaseController extends WebSocketController {
    public FightPhaseController(PlayerManager playerManager, EventManager eventManager) {
        super(playerManager, eventManager);
    }

    @MessageMapping("/resolveFight")
    public void resolveFight(SimpMessageHeaderAccessor headerAccessor, ResolveFightAction message) {
        relayActionToGame(ActionType.RESOLVE_FIGHT, message, headerAccessor);
    }
}
