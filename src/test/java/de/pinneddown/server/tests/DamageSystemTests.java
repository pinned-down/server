package de.pinneddown.server.tests;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.systems.DamageSystem;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class DamageSystemTests {
    @Test
    void createsDamageDeckAtStartOfGame() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();
        Random random = new Random();

        DamageSystem system = new DamageSystem(eventManager, entityManager, random);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        CardPileComponent cardPileComponent = entityManager.getComponent(0, CardPileComponent.class);

        assertThat(cardPileComponent).isNotNull();
        assertThat(cardPileComponent.getCardPile()).isNotNull();
        assertThat(cardPileComponent.getCardPile().getCards()).isNotEmpty();
    }
}
