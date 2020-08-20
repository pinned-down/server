package de.pinneddown.server;

import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.components.DistanceComponent;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.systems.JumpPhaseSystem;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class JumpPhaseSystemTests extends GameSystemTestSuite {
    @Test
    void createsLocationDeckAtStartOfGame() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        Blueprint locationBlueprint = new Blueprint();
        BlueprintManager blueprintManager = createMockBlueprintManager(entityManager, locationBlueprint);

        Random random = new Random();

        JumpPhaseSystem system = new JumpPhaseSystem(eventManager, entityManager, blueprintManager, random);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        CardPileComponent cardPileComponent = entityManager.getComponent(0, CardPileComponent.class);

        assertThat(cardPileComponent).isNotNull();
        assertThat(cardPileComponent.getCardPile()).isNotNull();
        assertThat(cardPileComponent.getCardPile().getCards()).isNotEmpty();
    }

    @Test
    void revealsInitialLocationAtStartOfGame() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        Blueprint locationBlueprint = new Blueprint();
        locationBlueprint.getComponents().add(DistanceComponent.class.getSimpleName());
        locationBlueprint.getAttributes().put("Distance", 1);

        BlueprintManager blueprintManager = createMockBlueprintManager(entityManager, locationBlueprint);

        Random random = new Random();

        JumpPhaseSystem system = new JumpPhaseSystem(eventManager, entityManager, blueprintManager, random);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        DistanceComponent distanceComponent = entityManager.getComponent(0, DistanceComponent.class);

        assertThat(distanceComponent).isNotNull();
        assertThat(distanceComponent.getDistance()).isGreaterThan(0);
    }
}
