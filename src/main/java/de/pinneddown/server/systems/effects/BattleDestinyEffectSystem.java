package de.pinneddown.server.systems.effects;

import de.pinneddown.server.*;
import de.pinneddown.server.components.BattleDestinyComponent;
import de.pinneddown.server.components.CardPileComponent;
import de.pinneddown.server.components.PowerComponent;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.events.AbilityEffectActivatedEvent;
import de.pinneddown.server.events.AbilityEffectDeactivatedEvent;
import de.pinneddown.server.events.AttackDeckInitializedEvent;
import de.pinneddown.server.events.BattleDestinyDrawnEvent;
import de.pinneddown.server.util.PowerUtils;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class BattleDestinyEffectSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;
    private PowerUtils powerUtils;
    private Random random;

    long attackDeckEntityId;

    public BattleDestinyEffectSystem(EventManager eventManager, EntityManager entityManager,
                                  BlueprintManager blueprintManager, PowerUtils powerUtils, Random random) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;
        this.powerUtils = powerUtils;
        this.random = random;

        this.eventManager.addEventHandler(EventType.ATTACK_DECK_INITIALIZED, this::onAttackDeckInitialized);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_ACTIVATED, this::onAbilityEffectActivated);
        this.eventManager.addEventHandler(EventType.ABILITY_EFFECT_DEACTIVATED, this::onAbilityEffectDeactivated);
    }

    private void onAttackDeckInitialized(GameEvent gameEvent) {
        AttackDeckInitializedEvent eventData = (AttackDeckInitializedEvent)gameEvent.getEventData();
        attackDeckEntityId = eventData.getEntityId();
    }

    private void onAbilityEffectActivated(GameEvent gameEvent) {
        AbilityEffectActivatedEvent eventData = (AbilityEffectActivatedEvent)gameEvent.getEventData();
        applyBattleDestinyPowerBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 1);
    }


    private void onAbilityEffectDeactivated(GameEvent gameEvent) {
        AbilityEffectDeactivatedEvent eventData = (AbilityEffectDeactivatedEvent)gameEvent.getEventData();
        applyBattleDestinyPowerBonus(eventData.getEffectEntityId(), eventData.getTargetEntityId(), 0);
    }

    private void applyBattleDestinyPowerBonus(long effectEntityId, long targetEntityId, int factor) {
        BattleDestinyComponent battleDestinyComponent = entityManager.getComponent(effectEntityId, BattleDestinyComponent.class);
        PowerComponent targetPowerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);

        if (battleDestinyComponent == null || targetPowerComponent == null) {
            return;
        }

        int battleDestiny = 0;

        if (factor != 0) {
            // Draw destiny.
            CardPileComponent attackDeck = entityManager.getComponent(attackDeckEntityId, CardPileComponent.class);

            if (attackDeck.getCardPile().isEmpty()) {
                if (attackDeck.getDiscardPile().isEmpty()) {
                    return;
                } else {
                    attackDeck.getDiscardPile().shuffleInto(attackDeck.getCardPile(), random);
                }
            }

            String destinyCardBlueprintId = attackDeck.getCardPile().pop();
            attackDeck.getDiscardPile().push(destinyCardBlueprintId);

            long destinyCardEntityId = blueprintManager.createEntity(destinyCardBlueprintId);

            ThreatComponent threatComponent = entityManager.getComponent(destinyCardEntityId, ThreatComponent.class);
            battleDestiny = threatComponent.getThreat();

            entityManager.removeEntity(destinyCardEntityId);

            // Notify listeners.
            eventManager.queueEvent(EventType.BATTLE_DESTINY_DRAWN, new BattleDestinyDrawnEvent
                    (targetEntityId, destinyCardBlueprintId, battleDestiny));
        }

        // Apply power bonus.
        int oldPowerModifier = targetPowerComponent.getPowerModifier();
        int newPowerModifier = oldPowerModifier
                - battleDestinyComponent.getAppliedBattleDestinyPower()
                + (battleDestiny * factor);

        battleDestinyComponent.setAppliedBattleDestinyPower(battleDestiny * factor);

        powerUtils.setPowerModifier(targetEntityId, newPowerModifier);
    }
}
