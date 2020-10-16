package de.pinneddown.server.systems.effects;

import de.pinneddown.server.*;
import de.pinneddown.server.components.AbilityEffectComponent;
import de.pinneddown.server.components.InstigatorComponent;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.components.ThreatChangeComponent;
import de.pinneddown.server.events.AbilityEffectActivatedEvent;
import de.pinneddown.server.util.ThreatUtils;
import org.springframework.stereotype.Component;

@Component
public class ThreatChangeEffectSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private ThreatUtils threatUtils;

    public ThreatChangeEffectSystem(EventManager eventManager, EntityManager entityManager, ThreatUtils threatUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.threatUtils = threatUtils;

        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::onAbilityEffectActivated);
    }

    private void onAbilityEffectActivated(GameEvent gameEvent) {
        AbilityEffectActivatedEvent eventData = (AbilityEffectActivatedEvent)gameEvent.getEventData();
        ThreatChangeComponent threatChangeComponent =
                entityManager.getComponent(eventData.getEffectEntityId(), ThreatChangeComponent.class);

        if (threatChangeComponent == null) {
            return;
        }

        // Get entity that owns the ability and effect.
        long effectEntityId = eventData.getEffectEntityId();
        InstigatorComponent instigatorComponent = entityManager.getComponent(effectEntityId, InstigatorComponent.class);
        long abilityEntityId = instigatorComponent != null ? instigatorComponent.getEntityId() : EntityManager.INVALID_ENTITY;
        OwnerComponent ownerComponent = entityManager.getComponent(abilityEntityId, OwnerComponent.class);
        long cardEntityId = ownerComponent != null ? ownerComponent.getOwner() : EntityManager.INVALID_ENTITY;

        threatUtils.addThreat(threatChangeComponent.getThreatChange(), ThreatChangeReason.EFFECT, cardEntityId);
    }
}
