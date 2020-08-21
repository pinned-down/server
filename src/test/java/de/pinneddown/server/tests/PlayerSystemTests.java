package de.pinneddown.server.tests;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.systems.PlayerSystem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerSystemTests {
    @Test
    void createsPlayerEntitiesAtStartOfGame() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        PlayerSystem system = new PlayerSystem(eventManager, entityManager);

        ArrayList<String> players = new ArrayList<>();
        players.add("A");
        players.add("B");

        // ACT
        ReadyToStartEvent eventData = new ReadyToStartEvent();
        eventData.setPlayers(players);

        eventManager.queueEvent(EventType.READY_TO_START, eventData);

        // ASSERT
        for (int i = 0; i < players.size(); ++i) {
            PlayerComponent playerComponent = entityManager.getComponent(i, PlayerComponent.class);

            assertThat(playerComponent).isNotNull();
            assertThat(playerComponent.getPlayerId()).isEqualTo(players.get(i));
        }
    }
}
