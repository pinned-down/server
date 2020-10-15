package de.pinneddown.server.systems.effects;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.GameEvent;
import de.pinneddown.server.components.OverloadComponent;
import de.pinneddown.server.events.AbilityEffectActivatedEvent;
import de.pinneddown.server.events.StarshipOverloadedEvent;
import org.springframework.stereotype.Component;

@Component
public class OverloadEffectSystem {
    private EventManager eventManager;
    private EntityManager entityManager;

    public OverloadEffectSystem(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::onAbilityEffectActivated);
    }

    private void onAbilityEffectActivated(GameEvent gameEvent) {
        AbilityEffectActivatedEvent eventData = (AbilityEffectActivatedEvent)gameEvent.getEventData();
        applyOverloads(eventData.getEffectEntityId(), eventData.getTargetEntityId());
    }

    private void applyOverloads(long effectEntityId, long targetEntityId) {
        OverloadComponent overloadComponent = entityManager.getComponent(effectEntityId, OverloadComponent.class);

        if (overloadComponent == null) {
            return;
        }

        for (int i = 0; i < overloadComponent.getOverloads(); ++i) {
            eventManager.queueEvent(EventType.STARSHIP_OVERLOADED, new StarshipOverloadedEvent(targetEntityId));
        }
    }
}
