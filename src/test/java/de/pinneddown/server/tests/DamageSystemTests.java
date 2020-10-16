package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.DefeatEvent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.events.StarshipDefeatedEvent;
import de.pinneddown.server.events.StarshipOverloadedEvent;
import de.pinneddown.server.systems.DamageSystem;
import de.pinneddown.server.util.GameplayTagUtils;
import de.pinneddown.server.util.PlayerUtils;
import de.pinneddown.server.util.PowerUtils;
import de.pinneddown.server.util.ThreatUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class DamageSystemTests {
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
        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, new StarshipDefeatedEvent(playerShipId, 0L));

        // ASSERT
        StructureComponent structureComponent = entityManager.getComponent(playerShipId, StructureComponent.class);

        assertThat(structureComponent.getStructureModifier()).isEqualTo(damage);
    }

    @Test
    void defeatedPlayerShipsTakeBonusDamage() {
        // ARRANGE
        // Setup system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        int damage = -10;

        DamageSystem system = createSystem(eventManager, entityManager, damage);

        eventManager.queueEvent(EventType.READY_TO_START, new ReadyToStartEvent());

        // Setup player ship.
        long playerShipId = createPlayerShip(entityManager, 0);

        // Setup enemy ship.
        long defeatedBy = entityManager.createEntity();
        DamageBonusComponent damageBonusComponent = new DamageBonusComponent();
        damageBonusComponent.setDamageBonus(2);
        entityManager.addComponent(defeatedBy, damageBonusComponent);

        // ACT
        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, new StarshipDefeatedEvent(playerShipId, defeatedBy));

        // ASSERT
        StructureComponent structureComponent = entityManager.getComponent(playerShipId, StructureComponent.class);

        assertThat(structureComponent.getStructureModifier())
                .isEqualTo(damage * (1 + damageBonusComponent.getDamageBonus()));
    }

    @Test
    void defeatedPlayerShipsLosePower() {
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
        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, new StarshipDefeatedEvent(playerShipId, 0L));

        // ASSERT
        PowerComponent powerComponent = entityManager.getComponent(playerShipId, PowerComponent.class);
        assertThat(powerComponent.getPowerModifier()).isLessThan(0);
    }

    @Test
    void defeatedPlayerShipsAreTagged() {
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
        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, new StarshipDefeatedEvent(playerShipId, 0L));

        // ASSERT
        GameplayTagsComponent gameplayTagsComponent = entityManager.getComponent(playerShipId, GameplayTagsComponent.class);

        assertThat(gameplayTagsComponent.getTemporaryGameplayTags()).contains(GameplayTags.STATUS_DAMAGED);
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
        StarshipDefeatedEvent starshipDefeatedEvent = new StarshipDefeatedEvent(playerShipId, 0L);
        starshipDefeatedEvent.setOverpowered(true);

        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, starshipDefeatedEvent);

        // ASSERT
        assertThat(entityManager.getComponent(playerShipId, StructureComponent.class)).isNull();
        assertThat(entityManager.getComponent(playerShipId, OwnerComponent.class)).isNull();
        assertThat(entityManager.getComponent(playerShipId, BlueprintComponent.class)).isNull();
    }

    @Test
    void overloadedShipsTakeDamage() {
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
        eventManager.queueEvent(EventType.STARSHIP_OVERLOADED, new StarshipOverloadedEvent(playerShipId));

        // ASSERT
        StructureComponent structureComponent = entityManager.getComponent(playerShipId, StructureComponent.class);

        assertThat(structureComponent.getStructureModifier()).isEqualTo(damage);
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
        StarshipDefeatedEvent starshipDefeatedEvent = new StarshipDefeatedEvent(playerShipId, 0L);
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
        GameSystemTestUtils testUtils = new GameSystemTestUtils();

        Blueprint damageBlueprint = new Blueprint();
        damageBlueprint.getComponents().add(StructureComponent.class.getSimpleName());
        damageBlueprint.getComponents().add(PowerComponent.class.getSimpleName());
        damageBlueprint.getAttributes().put("PowerModifier", -1);
        damageBlueprint.getAttributes().put("StructureModifier", damage);

        BlueprintManager blueprintManager = testUtils.createBlueprintManager(entityManager, damageBlueprint);

        Random random = new Random();

        ThreatUtils threatUtils = new ThreatUtils(eventManager, entityManager);
        PlayerUtils playerUtils = new PlayerUtils(eventManager, entityManager, blueprintManager, threatUtils);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);
        GameplayTagUtils gameplayTagUtils = new GameplayTagUtils(eventManager, entityManager);

        return new DamageSystem(eventManager, entityManager, blueprintManager, random, playerUtils, powerUtils,
                gameplayTagUtils);
    }

    private long createPlayerShip(EntityManager entityManager, long playerEntityId) {
        long playerShipId = entityManager.createEntity();

        PowerComponent powerComponent = new PowerComponent();
        powerComponent.setBasePower(5);
        entityManager.addComponent(playerShipId, powerComponent);

        StructureComponent structureComponent = new StructureComponent();
        structureComponent.setBaseStructure(100);
        entityManager.addComponent(playerShipId, structureComponent);

        OwnerComponent ownerComponent = new OwnerComponent();
        ownerComponent.setOwner(playerEntityId);
        entityManager.addComponent(playerShipId, ownerComponent);

        entityManager.addComponent(playerShipId, new BlueprintComponent());
        entityManager.addComponent(playerShipId, new GameplayTagsComponent());

        return playerShipId;
    }
}
