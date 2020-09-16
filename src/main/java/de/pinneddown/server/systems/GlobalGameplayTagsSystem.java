package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.GameplayTagsComponent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.events.CardRemovedEvent;
import de.pinneddown.server.events.GlobalGameplayTagsInitializedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.util.GameplayTagUtils;
import org.springframework.stereotype.Component;

@Component
public class GlobalGameplayTagsSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private GameplayTagUtils gameplayTagUtils;

    public GlobalGameplayTagsSystem(EventManager eventManager, EntityManager entityManager,
                                    GameplayTagUtils gameplayTagUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.gameplayTagUtils = gameplayTagUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);
        this.eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);
        this.eventManager.addEventHandler(EventType.CARD_REMOVED, this::onCardRemoved);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        // Set up global tags entity.
        long globalGameplayTagsEntityId = entityManager.createEntity();

        GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
        entityManager.addComponent(globalGameplayTagsEntityId, gameplayTagsComponent);

        // Notify listeners.
        GlobalGameplayTagsInitializedEvent eventData = new GlobalGameplayTagsInitializedEvent();
        eventData.setEntityId(globalGameplayTagsEntityId);

        eventManager.queueEvent(EventType.GLOBAL_GAMEPLAY_TAGS_INITIALIZED, eventData);
    }

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        // Remove old turn phase tag.
        gameplayTagUtils.removeGlobalGameplayTag(GameplayTags.TURNPHASE);

        // Add new turn phase tag.
        switch (eventData.getTurnPhase()) {
            case MAIN:
                gameplayTagUtils.addGlobalGameplayTag(GameplayTags.TURNPHASE_MAIN);
                break;

            case ATTACK:
                gameplayTagUtils.addGlobalGameplayTag(GameplayTags.TURNPHASE_ATTACK);
                break;

            case ASSIGNMENT:
                gameplayTagUtils.addGlobalGameplayTag(GameplayTags.TURNPHASE_ASSIGNMENT);
                break;

            case FIGHT:
                gameplayTagUtils.addGlobalGameplayTag(GameplayTags.TURNPHASE_FIGHT);
                break;

            case JUMP:
                gameplayTagUtils.addGlobalGameplayTag(GameplayTags.TURNPHASE_JUMP);
                break;
        }
    }

    private void onCardPlayed(GameEvent gameEvent) {
        CardPlayedEvent eventData = (CardPlayedEvent)gameEvent.getEventData();

        GameplayTagsComponent gameplayTagsComponent =
                entityManager.getComponent(eventData.getEntityId(), GameplayTagsComponent.class);

        if (gameplayTagsComponent == null) {
            return;
        }

        for (String gameplayTag : gameplayTagsComponent.getGlobalGameplayTags()) {
            gameplayTagUtils.addGlobalGameplayTag(gameplayTag);
        }
    }

    private void onCardRemoved(GameEvent gameEvent) {
        CardRemovedEvent eventData = (CardRemovedEvent)gameEvent.getEventData();

        GameplayTagsComponent gameplayTagsComponent =
                entityManager.getComponent(eventData.getEntityId(), GameplayTagsComponent.class);

        if (gameplayTagsComponent == null) {
            return;
        }

        for (String gameplayTag : gameplayTagsComponent.getGlobalGameplayTags()) {
            gameplayTagUtils.removeGlobalGameplayTag(gameplayTag);
        }
    }
}
