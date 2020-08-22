package de.pinneddown.server.controllers;

import de.pinneddown.server.ActionType;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.PlayerManager;
import de.pinneddown.server.actions.PlayerAction;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class WebSocketController {
    protected PlayerManager playerManager;
    protected EventManager eventManager;

    protected WebSocketController(PlayerManager playerManager, EventManager eventManager) {
        this.playerManager = playerManager;
        this.eventManager = eventManager;
    }

    protected void relayActionToGame(ActionType actionType, PlayerAction action, SimpMessageHeaderAccessor headerAccessor) {
        String remoteAddress = getRemoteAddressFromSession(headerAccessor);
        String playerId = playerManager.getPlayerIdFromRemoteAddress(remoteAddress);
        action.setPlayerId(playerId);

        eventManager.queueEvent(actionType, action);
    }

    protected String getRemoteAddressFromSession(SimpMessageHeaderAccessor headerAccessor) {
        return (String)headerAccessor.getSessionAttributes().get("remote-address");
    }
}
