package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ActivateAbilityAction;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.AbilityEffectActivatedEvent;
import de.pinneddown.server.events.AbilityEffectDeactivatedEvent;
import de.pinneddown.server.events.AttackDeckInitializedEvent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.systems.effects.*;
import de.pinneddown.server.util.GameplayTagUtils;
import de.pinneddown.server.util.PowerUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class AbilityEffectSystemTests {
    private GameSystemTestUtils testUtils;
    private boolean overloaded;

    @BeforeEach
    void beforeEach() {
        this.testUtils = new GameSystemTestUtils();
    }

    @Test
    void appliesPowerBonusEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);

        PowerBonusEffectSystem system = new PowerBonusEffectSystem(eventManager, entityManager, powerUtils);

        // Create effect.
        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
        PowerBonusComponent powerBonusComponent = new PowerBonusComponent();
        powerBonusComponent.setPowerBonus(2);
        entityManager.addComponent(effectEntityId, powerBonusComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();
        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                new AbilityEffectActivatedEvent(effectEntityId, null, null, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isGreaterThan(0);
    }

    @Test
    void removesPowerEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);

        PowerBonusEffectSystem system = new PowerBonusEffectSystem(eventManager, entityManager, powerUtils);

        // Create effect.
        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
        PowerBonusComponent powerBonusComponent = new PowerBonusComponent();
        powerBonusComponent.setPowerBonus(2);
        powerBonusComponent.setAppliedPowerBonus(2);
        entityManager.addComponent(effectEntityId, powerBonusComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();
        PowerComponent targetPowerComponent = new PowerComponent();
        targetPowerComponent.setPowerModifier(powerBonusComponent.getPowerBonus());

        entityManager.addComponent(targetEntityId, targetPowerComponent);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_DEACTIVATED,
                new AbilityEffectDeactivatedEvent(effectEntityId, targetEntityId));

        // ASSERT
        assertThat(targetPowerComponent.getPowerModifier()).isZero();
    }

    @Test
    void appliesPowerPerLocationEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);

        PowerPerLocationEffectSystem system = new PowerPerLocationEffectSystem(eventManager, entityManager, powerUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        // Create effect.
        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
        PowerPerLocationComponent powerPerLocationComponent = new PowerPerLocationComponent();
        powerPerLocationComponent.setPowerPerLocation(1);
        entityManager.addComponent(effectEntityId, powerPerLocationComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();
        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        eventManager.queueEvent(EventType.CURRENT_LOCATION_CHANGED, null);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                new AbilityEffectActivatedEvent(effectEntityId, null, null, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isGreaterThan(0);
    }

    @Test
    void appliesPowerPerAssignedThreatEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);

        PowerPerAssignedThreatEffectSystem system =
                new PowerPerAssignedThreatEffectSystem(eventManager, entityManager, powerUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        // Create effect.
        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
        PowerPerAssignedThreatComponent powerPerAssignedThreatComponent = new PowerPerAssignedThreatComponent();
        powerPerAssignedThreatComponent.setPowerPerThreat(1);
        entityManager.addComponent(effectEntityId, powerPerAssignedThreatComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        // Create assigned starship.
        long assignedTo = entityManager.createEntity();

        ThreatComponent threatComponent = new ThreatComponent();
        threatComponent.setThreat(2);
        entityManager.addComponent(assignedTo, threatComponent);

        AssignmentComponent assignmentComponent = new AssignmentComponent();
        assignmentComponent.setAssignedTo(assignedTo);
        entityManager.addComponent(targetEntityId, assignmentComponent);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                new AbilityEffectActivatedEvent(effectEntityId, null, null, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isEqualTo(threatComponent.getThreat());
    }

    @Test
    void appliesPowerPerFleetSizeEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);
        GameplayTagUtils gameplayTagUtils = new GameplayTagUtils(eventManager, entityManager);

        PowerPerFleetSizeEffectSystem system =
                new PowerPerFleetSizeEffectSystem(eventManager, entityManager, powerUtils, gameplayTagUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        // Create effect.
        String testFilterTag = "TestFilterTag";

        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
        PowerPerFleetSizeComponent powerPerFleetSizeComponent = new PowerPerFleetSizeComponent();
        powerPerFleetSizeComponent.setPowerPerFleetSize(2);
        powerPerFleetSizeComponent.setFleetGameplayTagFilter(testFilterTag);
        entityManager.addComponent(effectEntityId, powerPerFleetSizeComponent);

        // Create fleet.
        int fleetSize = 3;

        for (int i = 0; i < fleetSize; ++i) {
            long fleetEntityId = entityManager.createEntity();
            GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
            gameplayTagsComponent.setInitialGameplayTags(Lists.newArrayList(GameplayTags.CARDTYPE_STARSHIP, testFilterTag));
            entityManager.addComponent(fleetEntityId, gameplayTagsComponent);

            eventManager.queueEvent(EventType.CARD_PLAYED, new CardPlayedEvent(fleetEntityId, null, 0L));
        }

        // Create target.
        long targetEntityId = entityManager.createEntity();
        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                new AbilityEffectActivatedEvent(effectEntityId, null, null, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isEqualTo(powerPerFleetSizeComponent.getPowerPerFleetSize() * fleetSize);
    }

    @Test
    void appliesBattleDestinyEffect() {
        // ARRANGE
        int battleDestiny = 2;
        String battleDestinyCardBlueprintId = "TestCard";

        Blueprint destinyCardBlueprint = new Blueprint();
        destinyCardBlueprint.setId(battleDestinyCardBlueprintId);
        destinyCardBlueprint.getComponents().add(ThreatComponent.class.getSimpleName());
        destinyCardBlueprint.getAttributes().put("Threat", battleDestiny);

        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);
        BlueprintManager blueprintManager = testUtils.createBlueprintManager(entityManager, destinyCardBlueprint);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);
        Random random = new Random();

        BattleDestinyEffectSystem system = new BattleDestinyEffectSystem(eventManager, entityManager, blueprintManager, powerUtils, random);

        // Create attack deck.
        long attackDeckEntityId = entityManager.createEntity();
        CardPileComponent cardPileComponent = new CardPileComponent();
        cardPileComponent.getCardPile().push(battleDestinyCardBlueprintId);
        entityManager.addComponent(attackDeckEntityId, cardPileComponent);

        eventManager.queueEvent(EventType.ATTACK_DECK_INITIALIZED, new AttackDeckInitializedEvent(attackDeckEntityId));

        // Create effect.
        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
        BattleDestinyComponent battleDestinyComponent = new BattleDestinyComponent();
        battleDestinyComponent.setBattleDestinyCardsDrawn(1);
        entityManager.addComponent(effectEntityId, battleDestinyComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();
        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                new AbilityEffectActivatedEvent(effectEntityId, null, null, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isEqualTo(battleDestiny);
    }

    @Test
    void appliesOverloadEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        OverloadEffectSystem system = new OverloadEffectSystem(eventManager, entityManager);

        // Create effect.
        long effectEntityId = testUtils.createIndefiniteEffect(entityManager);
        OverloadComponent overloadComponent = new OverloadComponent();
        overloadComponent.setOverloads(1);
        entityManager.addComponent(effectEntityId, overloadComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        // Listen for events.
        overloaded = false;
        eventManager.addEventHandler(EventType.STARSHIP_OVERLOADED, this::onStarshipOverloaded);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_ACTIVATED,
                new AbilityEffectActivatedEvent(effectEntityId, null, null, targetEntityId));

        // ASSERT
        assertThat(overloaded).isTrue();
    }

    private void onStarshipOverloaded(GameEvent gameEvent) {
        overloaded = true;
    }
}
