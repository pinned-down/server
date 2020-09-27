package de.pinneddown.server;

public enum EventType {
    READY_TO_START,
    PLAYER_ENTITY_CREATED,
    TOTAL_DISTANCE_CHANGED,
    CARD_PLAYED,
    THREAT_POOL_INITIALIZED,
    STARSHIP_DEFEATED,
    STARSHIP_ASSIGNED,
    ATTACK_DECK_INITIALIZED,
    VICTORY,
    DEFEAT,
    PLAYER_HAND_CHANGED,
    CURRENT_LOCATION_CHANGED,
    THREAT_CHANGED,
    TURN_PHASE_STARTED,
    STARSHIP_DAMAGED,
    CARD_REMOVED,
    STARSHIP_POWER_CHANGED,
    ABILITY_EFFECT_APPLIED,
    ABILITY_EFFECT_REMOVED,
    GLOBAL_GAMEPLAY_TAGS_INITIALIZED,
    GLOBAL_GAMEPLAY_TAGS_CHANGED,
    PLAYER_DRAW_DECK_SIZE_CHANGED,
    PLAYER_DISCARD_PILE_CHANGED,
    STARSHIP_OVERLOADED,
    THREAT_MODIFIERS_CHANGED
}
