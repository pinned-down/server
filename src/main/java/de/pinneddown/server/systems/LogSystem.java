package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.services.MatchmakingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class LogSystem {
    private Logger logger = LoggerFactory.getLogger(LogSystem.class);

    public LogSystem(EventManager eventManager) {
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::logEvent);
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::logEvent);
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_DEACTIVATED, this::logEvent);
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_REMOVED, this::logEvent);
        eventManager.addEventHandler(EventType.ATTACK_DECK_INITIALIZED, this::logEvent);
        eventManager.addEventHandler(EventType.BATTLE_DESTINY_DRAWN, this::logEvent);
        eventManager.addEventHandler(EventType.CARD_PLAYED, this::logEvent);
        eventManager.addEventHandler(EventType.CARD_REMOVED, this::logEvent);
        eventManager.addEventHandler(EventType.CURRENT_LOCATION_CHANGED, this::logEvent);
        eventManager.addEventHandler(EventType.DEFEAT, this::logEvent);
        eventManager.addEventHandler(EventType.ERROR, this::logEvent);
        eventManager.addEventHandler(EventType.GAMEPLAY_TAGS_CHANGED, this::logEvent);
        eventManager.addEventHandler(EventType.GLOBAL_GAMEPLAY_TAGS_CHANGED, this::logEvent);
        eventManager.addEventHandler(EventType.GLOBAL_GAMEPLAY_TAGS_INITIALIZED, this::logEvent);
        eventManager.addEventHandler(EventType.PLAYER_DISCARD_PILE_CHANGED, this::logEvent);
        eventManager.addEventHandler(EventType.PLAYER_DRAW_DECK_SIZE_CHANGED, this::logEvent);
        eventManager.addEventHandler(EventType.PLAYER_ENTITY_CREATED, this::logEvent);
        eventManager.addEventHandler(EventType.PLAYER_HAND_CHANGED, this::logEvent);
        eventManager.addEventHandler(EventType.READY_TO_START, this::logEvent);
        eventManager.addEventHandler(EventType.STARSHIP_ASSIGNED, this::logEvent);
        eventManager.addEventHandler(EventType.STARSHIP_DAMAGED, this::logEvent);
        eventManager.addEventHandler(EventType.STARSHIP_DEFEATED, this::logEvent);
        eventManager.addEventHandler(EventType.STARSHIP_OVERLOADED, this::logEvent);
        eventManager.addEventHandler(EventType.STARSHIP_POWER_CHANGED, this::logEvent);
        eventManager.addEventHandler(EventType.THREAT_CHANGED, this::logEvent);
        eventManager.addEventHandler(EventType.THREAT_MODIFIERS_CHANGED, this::logEvent);
        eventManager.addEventHandler(EventType.THREAT_POOL_INITIALIZED, this::logEvent);
        eventManager.addEventHandler(EventType.TOTAL_DISTANCE_CHANGED, this::logEvent);
        eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::logEvent);
        eventManager.addEventHandler(EventType.VICTORY, this::logEvent);
    }

    private void logEvent(GameEvent gameEvent) {
        logger.info("{}", gameEvent.getEventData());
    }
}
