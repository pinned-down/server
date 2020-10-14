package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.EndMainPhaseAction;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.systems.MainPhaseSystem;
import de.pinneddown.server.util.PlayerUtils;
import de.pinneddown.server.util.ThreatUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MainPhaseSystemTests {
    private boolean mainPhaseEnded;

    @Test
    void mainPhaseEndsWhenAllPlayersAreReady() {
        // ARRANGE
        GameSystemTestUtils testUtils = new GameSystemTestUtils();

        // Set up system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        BlueprintManager blueprintManager = testUtils.createBlueprintManager(entityManager);
        PlayerManager playerManager = new PlayerManager();
        PlayerReadyManager playerReadyManager = new PlayerReadyManager(playerManager);
        ThreatUtils threatUtils = new ThreatUtils(eventManager, entityManager);
        PlayerUtils playerUtils = new PlayerUtils(eventManager, entityManager, blueprintManager, threatUtils);

        MainPhaseSystem system = new MainPhaseSystem(eventManager, entityManager, playerReadyManager, playerUtils);

        // Add single player.
        String playerId = "PlayerA";
        playerManager.addPlayer("", playerId);

        // Listen for resulting event.
        mainPhaseEnded = false;
        eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);

        // ACT
        EndMainPhaseAction actionData = new EndMainPhaseAction();
        actionData.setPlayerId(playerId);

        eventManager.queueEvent(ActionType.END_MAIN_PHASE, actionData);

        // ASSERT
        assertThat(mainPhaseEnded).isTrue();
    }

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        if (eventData.getTurnPhase() == TurnPhase.ATTACK) {
            mainPhaseEnded = true;
        }
    }
}
