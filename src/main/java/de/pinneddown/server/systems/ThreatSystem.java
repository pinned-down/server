package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.components.ThreatModifierComponent;
import de.pinneddown.server.events.*;
import de.pinneddown.server.util.ThreatUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ThreatSystem {
    private static final int INITIAL_THREAT = 2;

    private EventManager eventManager;
    private EntityManager entityManager;
    private ThreatUtils threatUtils;

    private long activeThreatModifiersEntityId;

    public ThreatSystem(EventManager eventManager, EntityManager entityManager, ThreatUtils threatUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.threatUtils = threatUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_REMOVED, this::onAbilityEffectRemoved);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        // Set up threat pool.
        long threatPoolEntityId = entityManager.createEntity();

        ThreatComponent threatComponent = new ThreatComponent();
        entityManager.addComponent(threatPoolEntityId, threatComponent);

        // Notify listeners.
        ThreatPoolInitializedEvent eventData = new ThreatPoolInitializedEvent();
        eventData.setEntityId(threatPoolEntityId);

        eventManager.queueEvent(EventType.THREAT_POOL_INITIALIZED, eventData);

        // Set initial threat.
        threatUtils.setThreat(threatPoolEntityId, INITIAL_THREAT);

        // Reset modifiers.
        activeThreatModifiersEntityId = entityManager.createEntity();
        ThreatModifierComponent threatModifierComponent = new ThreatModifierComponent();
        threatModifierComponent.setThreatModifiers(new HashMap<>());
        entityManager.addComponent(activeThreatModifiersEntityId, threatModifierComponent);
    }

    private void onAbilityEffectApplied(GameEvent gameEvent) {
        AbilityEffectAppliedEvent eventData = (AbilityEffectAppliedEvent)gameEvent.getEventData();

        ThreatModifierComponent threatModifierComponent =
                entityManager.getComponent(eventData.getEffectEntityId(), ThreatModifierComponent.class);

        if (threatModifierComponent == null) {
            return;
        }

        applyThreatModifiers(threatModifierComponent.getThreatModifiers(), 1);
    }

    private void onAbilityEffectRemoved(GameEvent gameEvent) {
        AbilityEffectRemovedEvent eventData = (AbilityEffectRemovedEvent)gameEvent.getEventData();

        ThreatModifierComponent threatModifierComponent =
                entityManager.getComponent(eventData.getEffectEntityId(), ThreatModifierComponent.class);

        if (threatModifierComponent == null) {
            return;
        }

        applyThreatModifiers(threatModifierComponent.getThreatModifiers(), -1);
    }

    private void applyThreatModifiers(HashMap<String, Integer> newModifiers, int factor) {
        ThreatModifierComponent threatModifierComponent =
                entityManager.getComponent(activeThreatModifiersEntityId, ThreatModifierComponent.class);

        HashMap<String, Integer> threatModifiers = threatModifierComponent.getThreatModifiers();

        // Apply modifiers.
        for (Map.Entry<String, Integer> modifier : newModifiers.entrySet()) {
            int oldModifier = threatModifiers.getOrDefault(modifier.getKey(), 0);
            int newModifier = oldModifier + (modifier.getValue() * factor);

            if (newModifier != 0) {
                threatModifiers.put(modifier.getKey(), newModifier);
            } else {
                threatModifiers.remove(modifier.getKey());
            }
        }

        // Notify listeners.
        eventManager.queueEvent(EventType.THREAT_MODIFIERS_CHANGED, new ThreatModifiersChangedEvent(threatModifiers));
    }
}
