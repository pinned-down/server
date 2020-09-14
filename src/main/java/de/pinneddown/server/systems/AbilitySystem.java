package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ActivateAbilityAction;
import de.pinneddown.server.components.AbilitiesComponent;
import de.pinneddown.server.components.AbilityComponent;
import de.pinneddown.server.components.AbilityEffectComponent;
import de.pinneddown.server.components.PowerComponent;
import de.pinneddown.server.events.AbilityEffectAppliedEvent;
import de.pinneddown.server.events.AbilityEffectRemovedEvent;
import de.pinneddown.server.events.CardRemovedEvent;
import de.pinneddown.server.events.StarshipPowerChangedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AbilitySystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;

    public AbilitySystem(EventManager eventManager, EntityManager entityManager, BlueprintManager blueprintManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;

        this.eventManager.addEventHandler(ActionType.ACTIVATE_ABILITY, this::onActivateAbility);
        this.eventManager.addEventHandler(EventType.CARD_REMOVED, this::onCardRemoved);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_REMOVED, this::onAbilityEffectRemoved);
    }

    private void onActivateAbility(GameEvent gameEvent) {
        ActivateAbilityAction eventData = (ActivateAbilityAction)gameEvent.getEventData();

        // Apply effects.
        AbilitiesComponent abilitiesComponent =
                entityManager.getComponent(eventData.getEntityId(), AbilitiesComponent.class);
        ArrayList<Long> abilityEntities = abilitiesComponent.getOrCreateAbilityEntities(blueprintManager);
        long abilityEntityId = abilityEntities.get(eventData.getAbilityIndex());

        AbilityComponent abilityComponent = entityManager.getComponent(abilityEntityId, AbilityComponent.class);

        for (String effectBlueprintId : abilityComponent.getAbilityEffects()) {
            long effectEntityId = blueprintManager.createEntity(effectBlueprintId);

            // Apply power bonus.
            applyPowerBonus(effectEntityId, eventData.getTargetEntityId(), 1);

            // Store target.
            AbilityEffectComponent abilityEffectComponent =
                    entityManager.getComponent(effectEntityId, AbilityEffectComponent.class);

            if (abilityEffectComponent != null) {
                abilityEffectComponent.setTargetEntityId(eventData.getTargetEntityId());
            }

            // Notify listeners.
            AbilityEffectAppliedEvent abilityEffectAppliedEvent =
                    new AbilityEffectAppliedEvent(effectEntityId, eventData.getTargetEntityId());
            eventManager.queueEvent(EventType.ABILITY_EFFECT_APPLIED, abilityEffectAppliedEvent);
        }
    }

    private void onAbilityEffectRemoved(GameEvent gameEvent) {
        AbilityEffectRemovedEvent eventData = (AbilityEffectRemovedEvent)gameEvent.getEventData();

        // Remove power bonus.
        applyPowerBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), -1);
    }

    private void onCardRemoved(GameEvent gameEvent) {
        CardRemovedEvent eventData = (CardRemovedEvent)gameEvent.getEventData();

        // Remove ability entities.
        AbilitiesComponent abilitiesComponent =
                entityManager.getComponent(eventData.getEntityId(), AbilitiesComponent.class);

        if (abilitiesComponent == null) {
            return;
        }

        ArrayList<Long> abilities = abilitiesComponent.getAbilityEntities();

        if (abilities == null) {
            return;
        }

        abilities.forEach(abilityEntity -> entityManager.removeEntity(abilityEntity));
    }

    private void applyPowerBonus(long effectEntityId, long targetEntityId, int powerFactor) {
        PowerComponent effectPowerComponent = entityManager.getComponent(effectEntityId, PowerComponent.class);
        PowerComponent targetPowerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);

        if (effectPowerComponent != null && targetPowerComponent != null) {
            int oldPowerModifier = targetPowerComponent.getPowerModifier();
            int newPowerModifier = oldPowerModifier + (effectPowerComponent.getPowerModifier() * powerFactor);

            targetPowerComponent.setPowerModifier(newPowerModifier);

            StarshipPowerChangedEvent starshipPowerChangedEvent =
                    new StarshipPowerChangedEvent(targetEntityId, oldPowerModifier, newPowerModifier);
            eventManager.queueEvent(EventType.STARSHIP_POWER_CHANGED, starshipPowerChangedEvent);
        }
    }
}
