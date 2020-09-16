package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.systems.GlobalGameplayTagsSystem;
import de.pinneddown.server.util.GameplayTagUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalGameplayTagsSystemTests {
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
}
