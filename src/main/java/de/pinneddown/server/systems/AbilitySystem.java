package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ActivateAbilityAction;
import de.pinneddown.server.components.AbilitiesComponent;
import de.pinneddown.server.components.AbilityComponent;
import de.pinneddown.server.components.PowerComponent;
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
            PowerComponent effectPowerComponent = entityManager.getComponent(effectEntityId, PowerComponent.class);
            PowerComponent targetPowerComponent = entityManager.getComponent(eventData.getTargetEntityId(), PowerComponent.class);

            if (effectPowerComponent != null && targetPowerComponent != null) {
                int oldPowerModifier = targetPowerComponent.getPowerModifier();
                int newPowerModifier = oldPowerModifier + effectPowerComponent.getPowerModifier();

                targetPowerComponent.setPowerModifier(newPowerModifier);

                StarshipPowerChangedEvent starshipPowerChangedEvent =
                        new StarshipPowerChangedEvent(eventData.getTargetEntityId(), oldPowerModifier, newPowerModifier);
                eventManager.queueEvent(EventType.STARSHIP_POWER_CHANGED, starshipPowerChangedEvent);
            }
        }
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
}
