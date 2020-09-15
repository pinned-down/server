package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ResolveFightAction;
import de.pinneddown.server.components.AssignmentComponent;
import de.pinneddown.server.components.BlueprintComponent;
import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.components.PowerComponent;
import de.pinneddown.server.events.*;
import de.pinneddown.server.util.AssignmentUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class FightPhaseSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private AssignmentUtils assignmentUtils;

    private long attackDeckEntityId;
    private HashSet<Long> assignedStarships;

    public FightPhaseSystem(EventManager eventManager, EntityManager entityManager, AssignmentUtils assignmentUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.assignmentUtils = assignmentUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.ATTACK_DECK_INITIALIZED, this::onAttackDeckInitialized);
        this.eventManager.addEventHandler(EventType.STARSHIP_ASSIGNED, this::onStarshipAssigned);
        this.eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);
        this.eventManager.addEventHandler(ActionType.RESOLVE_FIGHT, this::onResolveFight);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        assignedStarships = new HashSet<>();
    }

    private void onAttackDeckInitialized(GameEvent gameEvent) {
        AttackDeckInitializedEvent eventData = (AttackDeckInitializedEvent)gameEvent.getEventData();
        attackDeckEntityId = eventData.getEntityId();
    }

    private void onStarshipAssigned(GameEvent gameEvent) {
        StarshipAssignedEvent eventData = (StarshipAssignedEvent)gameEvent.getEventData();

        if (eventData.getAssignedTo() > 0) {
            assignedStarships.add(eventData.getAssignedStarship());
        }
    }

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        if (eventData.getTurnPhase() != TurnPhase.FIGHT) {
            return;
        }

        // Check if there's anything to do.
        if (assignedStarships.size() <= 0) {
            eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.JUMP));
        }
    }

    private void onResolveFight(GameEvent gameEvent) {
        ResolveFightAction eventData = (ResolveFightAction)gameEvent.getEventData();

        // Get fighting starships.
        long playerEntityId = eventData.getEntityId();
        AssignmentComponent assignmentComponent =
                entityManager.getComponent(playerEntityId, AssignmentComponent.class);

        if (assignmentComponent == null) {
            return;
        }

        long enemyEntityId = assignmentComponent.getAssignedTo();

        PowerComponent playerPowerComponent = entityManager.getComponent(playerEntityId, PowerComponent.class);
        PowerComponent enemyPowerComponent =
                entityManager.getComponent(enemyEntityId, PowerComponent.class);

        if (playerPowerComponent == null || enemyPowerComponent == null) {
            return;
        }

        // Compare power.
        if (playerPowerComponent.getCurrentPower() > enemyPowerComponent.getCurrentPower()) {
            // Discard enemy starship.
            CardPileComponent cardPileComponent = entityManager.getComponent(attackDeckEntityId, CardPileComponent.class);
            BlueprintComponent blueprintComponent = entityManager.getComponent(enemyEntityId, BlueprintComponent.class);

            cardPileComponent.getDiscardPile().push(blueprintComponent.getBlueprintId());

            eventManager.queueEvent(EventType.CARD_REMOVED, new CardRemovedEvent(enemyEntityId));
            entityManager.removeEntity(enemyEntityId);

            // Notify listeners.
            boolean overpowered = playerPowerComponent.getCurrentPower() >= enemyPowerComponent.getCurrentPower() * 2;

            onStarshipDefeated(enemyEntityId, overpowered);
        } else {
            // Damage or destroy player starship.
            boolean overpowered = enemyPowerComponent.getCurrentPower() >= playerPowerComponent.getCurrentPower() * 2;

            onStarshipDefeated(playerEntityId, overpowered);
        }

        // Unassign starship.
        assignmentUtils.assignTo(playerEntityId, -1);
        assignedStarships.remove(playerEntityId);

        // Check if all fights resolved.
        if (assignedStarships.size() <= 0) {
            eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.JUMP));
        }
    }

    private void onStarshipDefeated(long entityId, boolean overpowered) {
        StarshipDefeatedEvent starshipDefeatedEvent = new StarshipDefeatedEvent(entityId);
        starshipDefeatedEvent.setOverpowered(overpowered);

        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, starshipDefeatedEvent);
    }
}
