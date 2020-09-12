package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ResolveFightAction;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.AttackDeckInitializedEvent;
import de.pinneddown.server.events.ReadyToStartEvent;
import de.pinneddown.server.events.StarshipDefeatedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.systems.DamageSystem;
import de.pinneddown.server.systems.FightPhaseSystem;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class FightPhaseSystemTests {
    private StarshipDefeatedEvent eventData;
    private boolean fightPhaseEnded;

    @Test
    void defeatedEnemyShipsAreDestroyed() {
        // ARRANGE
        // Setup system.
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        createSystem(eventManager, entityManager);

        // Setup enemy ship.
        long enemyShipId = createStarship(entityManager, 1);

        // Setup player ship.
        long playerShipId = createStarship(entityManager, 2);

        AssignmentComponent assignmentComponent = new AssignmentComponent();
        assignmentComponent.setAssignedTo(enemyShipId);
        entityManager.addComponent(playerShipId, assignmentComponent);

        // ACT
        eventManager.queueEvent(ActionType.RESOLVE_FIGHT, new ResolveFightAction(playerShipId));

        // ASSERT
        assertThat(entityManager.getComponent(enemyShipId, PowerComponent.class)).isNull();
        assertThat(entityManager.getComponent(enemyShipId, BlueprintComponent.class)).isNull();
    }

    @Test
    void moreEnemyPowerDefeatsPlayerShip() {
        // ARRANGE
        // Setup system.
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        createSystem(eventManager, entityManager);

        // Setup enemy ship.
        long enemyShipId = createStarship(entityManager, 3);

        // Setup player ship.
        long playerShipId = createStarship(entityManager, 2);

        AssignmentComponent assignmentComponent = new AssignmentComponent();
        assignmentComponent.setAssignedTo(enemyShipId);
        entityManager.addComponent(playerShipId, assignmentComponent);

        // Register listener.
        eventData = null;
        eventManager.addEventHandler(EventType.STARSHIP_DEFEATED, this::onStarshipDefeated);

        // ACT
        eventManager.queueEvent(ActionType.RESOLVE_FIGHT, new ResolveFightAction(playerShipId));

        // ASSERT
        assertThat(eventData).isNotNull();
        assertThat(eventData.getEntityId()).isEqualTo(playerShipId);
        assertThat(eventData.isOverpowered()).isFalse();
    }

    @Test
    void doubleEnemyPowerOverpowersPlayerShip() {
        // ARRANGE
        // Setup system.
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        createSystem(eventManager, entityManager);

        // Setup enemy ship.
        long enemyShipId = createStarship(entityManager, 4);

        // Setup player ship.
        long playerShipId = createStarship(entityManager, 2);

        AssignmentComponent assignmentComponent = new AssignmentComponent();
        assignmentComponent.setAssignedTo(enemyShipId);
        entityManager.addComponent(playerShipId, assignmentComponent);

        // Register listener.
        eventData = null;
        eventManager.addEventHandler(EventType.STARSHIP_DEFEATED, this::onStarshipDefeated);

        // ACT
        eventManager.queueEvent(ActionType.RESOLVE_FIGHT, new ResolveFightAction(playerShipId));

        // ASSERT
        assertThat(eventData).isNotNull();
        assertThat(eventData.getEntityId()).isEqualTo(playerShipId);
        assertThat(eventData.isOverpowered()).isTrue();
    }

    @Test
    void fightPhaseIsSkippedWithoutAssignments() {
        // ARRANGE
        // Setup system.
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        createSystem(eventManager, entityManager);

        // Register listener.
        fightPhaseEnded = false;
        eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);

        // ACT
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.FIGHT));

        // ASSERT
        assertThat(fightPhaseEnded).isTrue();
    }

    @Test
    void fightPhaseEndsAfterAllAssignments() {
        // ARRANGE
        // Setup system.
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        createSystem(eventManager, entityManager);

        // Setup enemy ship.
        long enemyShipId = createStarship(entityManager, 1);

        // Setup player ship.
        long playerShipId = createStarship(entityManager, 1);

        AssignmentComponent assignmentComponent = new AssignmentComponent();
        assignmentComponent.setAssignedTo(enemyShipId);
        entityManager.addComponent(playerShipId, assignmentComponent);

        // Register listener.
        fightPhaseEnded = false;
        eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);

        // ACT
        eventManager.queueEvent(ActionType.RESOLVE_FIGHT, new ResolveFightAction(playerShipId));

        // ASSERT
        assertThat(fightPhaseEnded).isTrue();
    }

    private FightPhaseSystem createSystem(EventManager eventManager, EntityManager entityManager) {
        FightPhaseSystem system = new FightPhaseSystem(eventManager, entityManager);

        long attackDeckEntityId = entityManager.createEntity();
        entityManager.addComponent(attackDeckEntityId, new CardPileComponent());

        eventManager.queueEvent(EventType.READY_TO_START, null);
        eventManager.queueEvent(EventType.ATTACK_DECK_INITIALIZED, new AttackDeckInitializedEvent(attackDeckEntityId));

        return system;
    }

    private long createStarship(EntityManager entityManager, int power) {
        long entityId = entityManager.createEntity();

        PowerComponent powerComponent = new PowerComponent();
        powerComponent.setBasePower(power);
        entityManager.addComponent(entityId, powerComponent);
        entityManager.addComponent(entityId, new BlueprintComponent());

        return entityId;
    }

    private void onStarshipDefeated(GameEvent gameEvent) {
        eventData = (StarshipDefeatedEvent)gameEvent.getEventData();
    }

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        if (eventData.getTurnPhase() == TurnPhase.JUMP) {
            fightPhaseEnded = true;
        }
    }
}
