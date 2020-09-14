package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.DefeatEvent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.events.StarshipDefeatedEvent;
import de.pinneddown.server.systems.DamageSystem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class DamageSystemTests extends GameSystemTestSuite {
    private DefeatEvent defeatEvent;

    @Test
    void createsDamageDeckAtStartOfGame() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        int damage = -10;

        DamageSystem system = createSystem(eventManager, entityManager, damage);

        // ACT
        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // ASSERT
        CardPileComponent cardPileComponent = entityManager.getComponent(1L, CardPileComponent.class);

        assertThat(cardPileComponent).isNotNull();
        assertThat(cardPileComponent.getCardPile()).isNotNull();
        assertThat(cardPileComponent.getCardPile().getCards()).isNotEmpty();
    }

    @Test
    void defeatedPlayerShipsTakeDamage() {
        // ARRANGE
        // Setup system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        int damage = -10;

        DamageSystem system = createSystem(eventManager, entityManager, damage);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // Setup player ship.
        long playerShipId = createPlayerShip(entityManager, 0);

        // ACT
        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, new StarshipDefeatedEvent(playerShipId));

        // ASSERT
        StructureComponent structureComponent = entityManager.getComponent(playerShipId, StructureComponent.class);

        assertThat(structureComponent.getStructureModifier()).isEqualTo(damage);
    }

    @Test
    void overpoweredPlayerShipsAreDestroyed() {
        // ARRANGE
        // Setup system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        int damage = -10;

        DamageSystem system = createSystem(eventManager, entityManager, damage);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // Setup player and ship.
        long playerEntityId = entityManager.createEntity();
        entityManager.addComponent(playerEntityId, new PlayerComponent());

        long playerShipId = createPlayerShip(entityManager, playerEntityId);

        // ACT
        StarshipDefeatedEvent starshipDefeatedEvent = new StarshipDefeatedEvent(playerShipId);
        starshipDefeatedEvent.setOverpowered(true);

        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, starshipDefeatedEvent);

        // ASSERT
        assertThat(entityManager.getComponent(playerShipId, StructureComponent.class)).isNull();
        assertThat(entityManager.getComponent(playerShipId, OwnerComponent.class)).isNull();
        assertThat(entityManager.getComponent(playerShipId, BlueprintComponent.class)).isNull();
    }

    @Test
    void defeatWhenFlagshipDestroyed() {
        // ARRANGE
        // Setup system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        int damage = -10;

        DamageSystem system = createSystem(eventManager, entityManager, damage);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // Setup player and ship.
        long playerEntityId = entityManager.createEntity();
        entityManager.addComponent(playerEntityId, new PlayerComponent());

        long playerShipId = createPlayerShip(entityManager, playerEntityId);

        GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
        ArrayList<String> gameplayTags = new ArrayList<>();
        gameplayTags.add(GameplayTags.KEYWORD_FLAGSHIP);
        gameplayTagsComponent.setInitialGameplayTags(gameplayTags);
        entityManager.addComponent(playerShipId, gameplayTagsComponent);

        // Register listener.
        defeatEvent = null;
        eventManager.addEventHandler(EventType.DEFEAT, this::onDefeat);

        // ACT
        StarshipDefeatedEvent starshipDefeatedEvent = new StarshipDefeatedEvent(playerShipId);
        starshipDefeatedEvent.setOverpowered(true);

        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, starshipDefeatedEvent);

        // ASSERT
        assertThat(defeatEvent).isNotNull();
        assertThat(defeatEvent.getReason()).isEqualTo(DefeatReason.FLAGSHIP_DESTROYED);
        assertThat(defeatEvent.getEntityId()).isEqualTo(playerShipId);
    }

    private void onDefeat(GameEvent gameEvent) {
        defeatEvent = (DefeatEvent)gameEvent.getEventData();
    }

    private DamageSystem createSystem(EventManager eventManager, EntityManager entityManager, int damage) {
        Blueprint damageBlueprint = new Blueprint();
        damageBlueprint.getComponents().add(StructureComponent.class.getSimpleName());
        damageBlueprint.getAttributes().put("StructureModifier", damage);
        BlueprintManager blueprintManager = createMockBlueprintManager(entityManager, damageBlueprint);

        Random random = new Random();

        return new DamageSystem(eventManager, entityManager, blueprintManager, random);
    }

    private long createPlayerShip(EntityManager entityManager, long playerEntityId) {
        long playerShipId = entityManager.createEntity();

        StructureComponent structureComponent = new StructureComponent();
        structureComponent.setBaseStructure(100);
        entityManager.addComponent(playerShipId, structureComponent);

        OwnerComponent ownerComponent = new OwnerComponent();
        ownerComponent.setOwner(playerEntityId);
        entityManager.addComponent(playerShipId, ownerComponent);

        entityManager.addComponent(playerShipId, new BlueprintComponent());

        return playerShipId;
    }
}
