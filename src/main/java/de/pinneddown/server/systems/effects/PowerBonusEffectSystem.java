package de.pinneddown.server.systems.effects;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.GameEvent;
import de.pinneddown.server.components.PowerBonusComponent;
import de.pinneddown.server.components.PowerComponent;
import de.pinneddown.server.events.AbilityEffectActivatedEvent;
import de.pinneddown.server.events.AbilityEffectDeactivatedEvent;
import de.pinneddown.server.util.PowerUtils;
import org.springframework.stereotype.Component;

@Component
public class PowerBonusEffectSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private PowerUtils powerUtils;

    public PowerBonusEffectSystem(EventManager eventManager, EntityManager entityManager, PowerUtils powerUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.powerUtils = powerUtils;

        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::onAbilityEffectActivated);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_DEACTIVATED, this::onAbilityEffectDeactivated);
    }

    private void onAbilityEffectActivated(GameEvent gameEvent) {
        AbilityEffectActivatedEvent eventData = (AbilityEffectActivatedEvent)gameEvent.getEventData();
        applyPowerBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 1);
    }

    private void onAbilityEffectDeactivated(GameEvent gameEvent) {
        AbilityEffectDeactivatedEvent eventData = (AbilityEffectDeactivatedEvent)gameEvent.getEventData();
        applyPowerBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 0);
    }

    private void applyPowerBonus(long effectEntityId, long targetEntityId, int powerFactor) {
        PowerBonusComponent powerBonusComponent = entityManager.getComponent(effectEntityId, PowerBonusComponent.class);
        PowerComponent targetPowerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);

        if (powerBonusComponent != null && targetPowerComponent != null) {
            int oldPowerModifier = targetPowerComponent.getPowerModifier();
            int newPowerModifier = oldPowerModifier
                    - powerBonusComponent.getAppliedPowerBonus()
                    + (powerBonusComponent.getPowerBonus() * powerFactor);

            powerBonusComponent.setAppliedPowerBonus(powerBonusComponent.getPowerBonus() * powerFactor);

            powerUtils.setPowerModifier(targetEntityId, newPowerModifier);
        }
    }
}
