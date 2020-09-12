package de.pinneddown.server.controllers;

import de.pinneddown.server.ActionType;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.PlayerManager;
import de.pinneddown.server.actions.PlayEffectAction;
import de.pinneddown.server.actions.ResolveFightAction;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class PlayEffectController extends WebSocketController {
    public PlayEffectController(PlayerManager playerManager, EventManager eventManager) {
        super(playerManager, eventManager);
    }

    @MessageMapping("/playEffect")
    public void playEffect(SimpMessageHeaderAccessor headerAccessor, PlayEffectAction message) {
        relayActionToGame(ActionType.PLAY_EFFECT, message, headerAccessor);
    }
}
