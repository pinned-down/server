package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.events.StarshipDefeatedEvent;
import de.pinneddown.server.systems.DamageSystem;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class DamageSystemTests extends GameSystemTestSuite {
    @Test
    void createsDamageDeckAtStartOfGame() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();
        BlueprintManager blueprintManager = createMockBlueprintManager(entityManager, null);
        Random random = new Random();

        DamageSystem system = new DamageSystem(eventManager, entityManager, blueprintManager, random);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        CardPileComponent cardPileComponent = entityManager.getComponent(0, CardPileComponent.class);

        assertThat(cardPileComponent).isNotNull();
        assertThat(cardPileComponent.getCardPile()).isNotNull();
        assertThat(cardPileComponent.getCardPile().getCards()).isNotEmpty();
    }

    @Test
    void defeatedPlayerShipsTakeDamage() {
        // ARRANGE
        // Setup system.
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        int damage = -10;
        Blueprint damageBlueprint = new Blueprint();
        damageBlueprint.getComponents().add(StructureComponent.class.getSimpleName());
        damageBlueprint.getAttributes().put("StructureModifier", damage);
        BlueprintManager blueprintManager = createMockBlueprintManager(entityManager, damageBlueprint);

        Random random = new Random();

        DamageSystem system = new DamageSystem(eventManager, entityManager, blueprintManager, random);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // Setup player ship.
        long playerShipId = entityManager.createEntity();
        StructureComponent structureComponent = new StructureComponent();
        structureComponent.setBaseStructure(100);
        entityManager.addComponent(playerShipId, structureComponent);
        entityManager.addComponent(playerShipId, new OwnerComponent());
        entityManager.addComponent(playerShipId, new BlueprintComponent());

        // ACT
        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, new StarshipDefeatedEvent(playerShipId));

        // ASSERT
        assertThat(structureComponent.getStructureModifier()).isEqualTo(damage);
    }

    @Test
    void overpoweredPlayerShipsAreDestroyed() {
        // ARRANGE
        // Setup system.
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        Blueprint damageBlueprint = new Blueprint();
        damageBlueprint.getComponents().add(StructureComponent.class.getSimpleName());
        BlueprintManager blueprintManager = createMockBlueprintManager(entityManager, damageBlueprint);

        Random random = new Random();

        DamageSystem system = new DamageSystem(eventManager, entityManager, blueprintManager, random);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // Setup player.
        long playerEntityId = entityManager.createEntity();
        entityManager.addComponent(playerEntityId, new PlayerComponent());

        // Setup player ship.
        long playerShipId = entityManager.createEntity();

        StructureComponent structureComponent = new StructureComponent();
        structureComponent.setBaseStructure(100);
        entityManager.addComponent(playerShipId, structureComponent);

        OwnerComponent ownerComponent = new OwnerComponent();
        ownerComponent.setOwner(playerEntityId);
        entityManager.addComponent(playerShipId, ownerComponent);

        entityManager.addComponent(playerShipId, new BlueprintComponent());

        // ACT
        StarshipDefeatedEvent starshipDefeatedEvent = new StarshipDefeatedEvent(playerShipId);
        starshipDefeatedEvent.setOverpowered(true);

        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, starshipDefeatedEvent);

        // ASSERT
        assertThat(entityManager.getComponent(playerShipId, StructureComponent.class)).isNull();
        assertThat(entityManager.getComponent(playerShipId, OwnerComponent.class)).isNull();
        assertThat(entityManager.getComponent(playerShipId, BlueprintComponent.class)).isNull();
    }
}
