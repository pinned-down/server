package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.AbilityEffectComponent;
import de.pinneddown.server.events.AbilityEffectAppliedEvent;
import de.pinneddown.server.events.AbilityEffectRemovedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AbilityEffectDurationSystem {
    private EventManager eventManager;
    private EntityManager entityManager;

    private ArrayList<Long> effectsExpiringAtEndOfFight;

    public AbilityEffectDurationSystem(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);
        this.eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        this.effectsExpiringAtEndOfFight = new ArrayList<>();
    }

    private void onAbilityEffectApplied(GameEvent gameEvent) {
        AbilityEffectAppliedEvent eventData = (AbilityEffectAppliedEvent)gameEvent.getEventData();

        AbilityEffectComponent abilityEffectComponent =
                entityManager.getComponent(eventData.getEffectEntityId(), AbilityEffectComponent.class);

        switch (abilityEffectComponent.getDuration()) {
            case END_OF_FIGHT:
                effectsExpiringAtEndOfFight.add(eventData.getEffectEntityId());
                break;
        }
    }

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        if (eventData.getTurnPhase() == TurnPhase.JUMP) {
            for (long effectEntityId : effectsExpiringAtEndOfFight) {
                entityManager.removeEntity(effectEntityId);

                // Notify listeners.
                AbilityEffectComponent abilityEffectComponent =
                        entityManager.getComponent(effectEntityId, AbilityEffectComponent.class);

                AbilityEffectRemovedEvent abilityEffectRemovedEvent =
                        new AbilityEffectRemovedEvent(effectEntityId, abilityEffectComponent.getTargetEntityId());
                eventManager.queueEvent(EventType.ABILITY_EFFECT_REMOVED, abilityEffectRemovedEvent);
            }

            effectsExpiringAtEndOfFight.clear();
        }
    }
}
