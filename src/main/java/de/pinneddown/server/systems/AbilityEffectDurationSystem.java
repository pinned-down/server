package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.AbilityEffectComponent;
import de.pinneddown.server.events.AbilityEffectAppliedEvent;
import de.pinneddown.server.events.AbilityEffectRemovedEvent;
import de.pinneddown.server.events.CardRemovedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AbilityEffectDurationSystem {
    private EventManager eventManager;
    private EntityManager entityManager;

    private ArrayList<Long> effectEntities;
    private ArrayList<Long> effectsExpiringAtEndOfFight;

    public AbilityEffectDurationSystem(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_REMOVED, this::onAbilityEffectRemoved);
        this.eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);
        this.eventManager.addEventHandler(EventType.CARD_REMOVED, this::onCardRemoved);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        this.effectEntities = new ArrayList<>();
        this.effectsExpiringAtEndOfFight = new ArrayList<>();
    }

    private void onAbilityEffectApplied(GameEvent gameEvent) {
        AbilityEffectAppliedEvent eventData = (AbilityEffectAppliedEvent)gameEvent.getEventData();

        effectEntities.add(eventData.getEffectEntityId());

        AbilityEffectComponent abilityEffectComponent =
                entityManager.getComponent(eventData.getEffectEntityId(), AbilityEffectComponent.class);

        switch (abilityEffectComponent.getDuration()) {
            case END_OF_FIGHT:
                effectsExpiringAtEndOfFight.add(eventData.getEffectEntityId());
                break;
        }
    }

    private void onAbilityEffectRemoved(GameEvent gameEvent) {
        AbilityEffectRemovedEvent eventData = (AbilityEffectRemovedEvent)gameEvent.getEventData();

        effectEntities.remove(eventData.getEffectEntityId());
        effectsExpiringAtEndOfFight.remove(eventData.getEffectEntityId());
    }

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        if (eventData.getTurnPhase() == TurnPhase.JUMP) {
            for (long effectEntityId : effectsExpiringAtEndOfFight) {
                removeEffect(effectEntityId);
            }
        }
    }

    private void onCardRemoved(GameEvent gameEvent) {
        CardRemovedEvent eventData = (CardRemovedEvent)gameEvent.getEventData();

        // Remove indefinite effects.
        for (long effectEntityId : effectEntities) {
            AbilityEffectComponent abilityEffectComponent =
                    entityManager.getComponent(effectEntityId, AbilityEffectComponent.class);

            if (abilityEffectComponent.getDuration() == AbilityEffectDuration.INDEFINITE &&
                abilityEffectComponent.getTargetEntityId() == eventData.getEntityId()) {
                removeEffect(effectEntityId);
            }
        }
    }

    private void removeEffect(long effectEntityId) {
        entityManager.removeEntity(effectEntityId);

        // Notify listeners.
        AbilityEffectComponent abilityEffectComponent =
                entityManager.getComponent(effectEntityId, AbilityEffectComponent.class);

        AbilityEffectRemovedEvent abilityEffectRemovedEvent =
                new AbilityEffectRemovedEvent(effectEntityId, abilityEffectComponent.getTargetEntityId());
        eventManager.queueEvent(EventType.ABILITY_EFFECT_REMOVED, abilityEffectRemovedEvent);
    }
}
