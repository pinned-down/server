package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.GameplayTagsComponent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.events.CardRemovedEvent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.systems.GlobalGameplayTagsSystem;
import de.pinneddown.server.util.GameplayTagUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalGameplayTagsSystemTests {
    private static final String TEST_GAMEPLAY_TAG = "Test";

    @Test
    void setsTurnPhaseTag() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        GameplayTagUtils gameplayTagUtils = new GameplayTagUtils(eventManager, entityManager);

        GlobalGameplayTagsSystem system = new GlobalGameplayTagsSystem(eventManager, entityManager, gameplayTagUtils);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ACT
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.ATTACK));
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.ASSIGNMENT));

        // ASSERT
        assertThat(gameplayTagUtils.getGameplayTags(0)).doesNotContain(GameplayTags.TURNPHASE_ATTACK);
        assertThat(gameplayTagUtils.getGameplayTags(0)).contains(GameplayTags.TURNPHASE_ASSIGNMENT);
    }

    @Test
    void cardsAddGlobalTags() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        GameplayTagUtils gameplayTagUtils = new GameplayTagUtils(eventManager, entityManager);

        GlobalGameplayTagsSystem system = new GlobalGameplayTagsSystem(eventManager, entityManager, gameplayTagUtils);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ACT
        long entityId = entityManager.createEntity();
        GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
        gameplayTagsComponent.getGlobalGameplayTags().add(TEST_GAMEPLAY_TAG);
        entityManager.addComponent(entityId, gameplayTagsComponent);

        eventManager.queueEvent(EventType.CARD_PLAYED, new CardPlayedEvent(entityId, null, 0));

        // ASSERT
        assertThat(gameplayTagUtils.getGameplayTags(0)).contains(TEST_GAMEPLAY_TAG);
    }

    @Test
    void cardsRemoveGlobalTags() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        GameplayTagUtils gameplayTagUtils = new GameplayTagUtils(eventManager, entityManager);

        GlobalGameplayTagsSystem system = new GlobalGameplayTagsSystem(eventManager, entityManager, gameplayTagUtils);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        gameplayTagUtils.addGlobalGameplayTag(TEST_GAMEPLAY_TAG);

        // ACT
        long entityId = entityManager.createEntity();
        GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
        gameplayTagsComponent.getGlobalGameplayTags().add(TEST_GAMEPLAY_TAG);
        entityManager.addComponent(entityId, gameplayTagsComponent);

        eventManager.queueEvent(EventType.CARD_REMOVED, new CardRemovedEvent(entityId));

        // ASSERT
        assertThat(gameplayTagUtils.getGameplayTags(0)).doesNotContain(TEST_GAMEPLAY_TAG);
    }
}
