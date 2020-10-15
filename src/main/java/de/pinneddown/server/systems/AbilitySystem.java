package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ActivateAbilityAction;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;

@Component
public class AbilitySystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;

    private HashSet<Long> fightAbilityEntities;

    public AbilitySystem(EventManager eventManager, EntityManager entityManager, BlueprintManager blueprintManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(ActionType.ACTIVATE_ABILITY, this::onActivateAbility);
        this.eventManager.addEventHandler(EventType.CARD_PLAYED, this::onCardPlayed);
        this.eventManager.addEventHandler(EventType.CARD_REMOVED, this::onCardRemoved);
        this.eventManager.addEventHandler(EventType.STARSHIP_DEFEATED, this::onStarshipDefeated);
        this.eventManager.addEventHandler(EventType.TURN_PHASE_STARTED, this::onTurnPhaseStarted);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        fightAbilityEntities = new HashSet<>();
    }

    private void onActivateAbility(GameEvent gameEvent) {
        ActivateAbilityAction eventData = (ActivateAbilityAction)gameEvent.getEventData();

        AbilitiesComponent abilitiesComponent =
                entityManager.getComponent(eventData.getEntityId(), AbilitiesComponent.class);
        ArrayList<Long> abilityEntities = abilitiesComponent.getOrCreateAbilityEntities(blueprintManager);
        long abilityEntityId = abilityEntities.get(eventData.getAbilityIndex());

        activateAbility(abilityEntityId, eventData.getTargetEntityId());
    }

    private void onCardPlayed(GameEvent gameEvent) {
        CardPlayedEvent cardPlayedEvent = (CardPlayedEvent)gameEvent.getEventData();
        long entityId = cardPlayedEvent.getEntityId();

        AbilitiesComponent abilitiesComponent =
                entityManager.getComponent(entityId, AbilitiesComponent.class);

        if (abilitiesComponent == null) {
            return;
        }

        ArrayList<Long> abilityEntities = abilitiesComponent.getOrCreateAbilityEntities(blueprintManager);

        for (long abilityEntityId : abilityEntities) {
            AbilityComponent abilityComponent = entityManager.getComponent(abilityEntityId, AbilityComponent.class);

            if (abilityComponent.getAbilityActivationTypeEnum() == AbilityActivationType.FIGHT) {
                fightAbilityEntities.add(entityId);
                continue;
            }

            if (abilityComponent.getAbilityActivationTypeEnum() != AbilityActivationType.PASSIVE) {
                continue;
            }

            activateAbility(abilityEntityId, entityId);
        }
    }

    private void onCardRemoved(GameEvent gameEvent) {
        CardRemovedEvent eventData = (CardRemovedEvent)gameEvent.getEventData();

        fightAbilityEntities.remove(eventData.getEntityId());

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

    private void onStarshipDefeated(GameEvent gameEvent) {
        StarshipDefeatedEvent eventData = (StarshipDefeatedEvent)gameEvent.getEventData();

        AbilitiesComponent abilitiesComponent =
                entityManager.getComponent(eventData.getDefeatedBy(), AbilitiesComponent.class);

        if (abilitiesComponent == null) {
            return;
        }

        ArrayList<Long> abilityEntities = abilitiesComponent.getOrCreateAbilityEntities(blueprintManager);

        for (long abilityEntityId : abilityEntities) {
            AbilityComponent abilityComponent = entityManager.getComponent(abilityEntityId, AbilityComponent.class);

            if (abilityComponent.getAbilityActivationTypeEnum() != AbilityActivationType.DOMINANT) {
                continue;
            }

            long targetEntityId = abilityComponent.getTargetTypeEnum() == TargetType.ASSIGNED_TO
                    ? eventData.getEntityId() : eventData.getDefeatedBy();
            activateAbility(abilityEntityId, targetEntityId);
        }
    }

    private void onTurnPhaseStarted(GameEvent gameEvent) {
        TurnPhaseStartedEvent eventData = (TurnPhaseStartedEvent)gameEvent.getEventData();

        if (eventData.getTurnPhase() != TurnPhase.FIGHT) {
            return;
        }

        // Active fight abilities.
        for (long entityId : fightAbilityEntities) {
            AbilitiesComponent abilitiesComponent =
                    entityManager.getComponent(entityId, AbilitiesComponent.class);

            ArrayList<Long> abilityEntities = abilitiesComponent.getOrCreateAbilityEntities(blueprintManager);

            for (long abilityEntityId : abilityEntities) {
                AbilityComponent abilityComponent = entityManager.getComponent(abilityEntityId, AbilityComponent.class);

                if (abilityComponent.getAbilityActivationTypeEnum() != AbilityActivationType.FIGHT) {
                    continue;
                }

                activateAbility(abilityEntityId, entityId);
            }
        }
    }

    private void activateAbility(long abilityEntityId, long targetEntityId) {
        AbilityComponent abilityComponent = entityManager.getComponent(abilityEntityId, AbilityComponent.class);

        // Apply effects.
        for (String effectBlueprintId : abilityComponent.getAbilityEffects()) {
            long effectEntityId = blueprintManager.createEntity(effectBlueprintId);

            // Store instigator.
            InstigatorComponent instigatorComponent = new InstigatorComponent();
            instigatorComponent.setEntityId(abilityEntityId);
            entityManager.addComponent(effectEntityId, instigatorComponent);

            // Store target.
            AbilityEffectComponent abilityEffectComponent =
                    entityManager.getComponent(effectEntityId, AbilityEffectComponent.class);

            if (abilityEffectComponent != null) {
                abilityEffectComponent.setTargetEntityId(targetEntityId);
            }

            // Notify listeners.
            AbilityEffectAppliedEvent abilityEffectAppliedEvent =
                    new AbilityEffectAppliedEvent(effectEntityId, targetEntityId);
            eventManager.queueEvent(EventType.ABILITY_EFFECT_APPLIED, abilityEffectAppliedEvent);
        }
    }
}
