package de.pinneddown.server.util;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.components.PowerComponent;
import de.pinneddown.server.events.StarshipPowerChangedEvent;
import org.springframework.stereotype.Component;

@Component
public class PowerUtils {
    private EventManager eventManager;
    private EntityManager entityManager;

    public PowerUtils(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
    }

    public void setPowerModifier(long entityId, int newPowerModifier) {
        PowerComponent powerComponent = entityManager.getComponent(entityId, PowerComponent.class);

        int oldPowerModifier = powerComponent.getPowerModifier();

        if (oldPowerModifier == newPowerModifier) {
            return;
        }

        powerComponent.setPowerModifier(newPowerModifier);

        // Notify listeners.
        StarshipPowerChangedEvent starshipPowerChangedEvent =
                new StarshipPowerChangedEvent(entityId, oldPowerModifier, newPowerModifier);
        eventManager.queueEvent(EventType.STARSHIP_POWER_CHANGED, starshipPowerChangedEvent);
    }
}
