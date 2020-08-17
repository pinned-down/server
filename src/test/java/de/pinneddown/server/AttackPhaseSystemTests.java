package de.pinneddown.server;

import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.systems.AttackPhaseSystem;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class AttackPhaseSystemTests {
    @Test
    void createsAttackDeckAtStartOfGame() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();
        Random random = new Random();

        AttackPhaseSystem system = new AttackPhaseSystem(eventManager, entityManager, random);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        CardPileComponent cardPileComponent = entityManager.getComponent(0, CardPileComponent.class);

        assertThat(cardPileComponent).isNotNull();
        assertThat(cardPileComponent.getCardPile()).isNotNull();
        assertThat(cardPileComponent.getCardPile().getCards()).isNotEmpty();
    }
}
