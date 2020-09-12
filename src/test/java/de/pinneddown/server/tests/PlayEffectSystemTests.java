package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.PlayEffectAction;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import de.pinneddown.server.events.ThreatPoolInitializedEvent;
import de.pinneddown.server.systems.PlayEffectSystem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayEffectSystemTests extends GameSystemTestSuite {
    private static final String ABILITY_BLUEPRINT_ID = "testAbility";;
    private static final String EFFECT_BLUEPRINT_ID = "testEffect";
    private static final String EFFECT_CARD_BLUEPRINT_ID = "testEffectCard";

    @Test
    void removesCardFromHand() {
        // ARRANGE
        // Create system.
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        createSystem(entityManager, eventManager);
        setupThreatPool(entityManager, eventManager);

        // Setup player.
        String playerId = "player";
        long playerEntityId = setupPlayer(entityManager, eventManager, playerId, EFFECT_CARD_BLUEPRINT_ID);

        // Setup target.
        long targetEntityId = createTarget(entityManager);

        // ACT
        playEffect(eventManager, playerId, EFFECT_CARD_BLUEPRINT_ID, targetEntityId);

        // ASSERT
        PlayerComponent playerComponent = entityManager.getComponent(playerEntityId, PlayerComponent.class);
        assertThat(playerComponent.getHand().getCards()).doesNotContain(EFFECT_CARD_BLUEPRINT_ID);
    }

    @Test
    void increasesThreat() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        createSystem(entityManager, eventManager);

        long threatPoolEntityId = setupThreatPool(entityManager, eventManager);

        // Setup player.
        String playerId = "player";
        setupPlayer(entityManager, eventManager, playerId, EFFECT_CARD_BLUEPRINT_ID);

        // Setup target.
        long targetEntityId = createTarget(entityManager);

        // Check threat.
        ThreatComponent threatComponent = entityManager.getComponent(threatPoolEntityId, ThreatComponent.class);
        int oldThreat = threatComponent.getThreat();

        // ACT
        playEffect(eventManager, playerId, EFFECT_CARD_BLUEPRINT_ID, targetEntityId);

        // ASSERT
        int newThreat = threatComponent.getThreat();
        assertThat(newThreat).isGreaterThan(oldThreat);
    }

    @Test
    void appliesPowerEffect() {
        // ARRANGE
        EntityManager entityManager = new EntityManager();
        EventManager eventManager = new EventManager();

        createSystem(entityManager, eventManager);
        setupThreatPool(entityManager, eventManager);

        // Setup player.
        String playerId = "player";
        setupPlayer(entityManager, eventManager, playerId, EFFECT_CARD_BLUEPRINT_ID);

        // Setup target.
        long targetEntityId = createTarget(entityManager);

        // ACT
        playEffect(eventManager, playerId, EFFECT_CARD_BLUEPRINT_ID, targetEntityId);

        // ASSERT
        PowerComponent powerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);
        assertThat(powerComponent.getPowerModifier()).isGreaterThan(0);
    }

    private BlueprintManager createBlueprintManager(EntityManager entityManager) {
        // Create effect.
        Blueprint effectBlueprint = new Blueprint();
        effectBlueprint.getComponents().add(PowerComponent.class.getSimpleName());
        effectBlueprint.getAttributes().put("PowerModifier", 1);

        // Create ability.
        ArrayList<String> effects = new ArrayList<>();
        effects.add(EFFECT_BLUEPRINT_ID);

        Blueprint abilityBlueprint = new Blueprint();
        abilityBlueprint.getComponents().add(AbilityComponent.class.getSimpleName());
        abilityBlueprint.getAttributes().put("AbilityEffects", effects);

        // Create effect card.
        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(ABILITY_BLUEPRINT_ID);

        Blueprint effectCardBlueprint = new Blueprint();
        effectCardBlueprint.getComponents().add(ThreatComponent.class.getSimpleName());
        effectCardBlueprint.getAttributes().put("Threat", 1);
        effectCardBlueprint.getComponents().add(AbilitiesComponent.class.getSimpleName());
        effectCardBlueprint.getAttributes().put("Abilities", abilities);

        // Create blueprint manager.
        BlueprintSet blueprints = mock(BlueprintSet.class);

        when(blueprints.getBlueprint(ABILITY_BLUEPRINT_ID)).thenReturn(abilityBlueprint);
        when(blueprints.getBlueprint(EFFECT_BLUEPRINT_ID)).thenReturn(effectBlueprint);
        when(blueprints.getBlueprint(EFFECT_CARD_BLUEPRINT_ID)).thenReturn(effectCardBlueprint);

        BlueprintManager blueprintManager = new BlueprintManager(entityManager);
        blueprintManager.setBlueprints(blueprints);

        return blueprintManager;
    }

    private PlayEffectSystem createSystem(EntityManager entityManager, EventManager eventManager) {
        BlueprintManager blueprintManager = createBlueprintManager(entityManager);
        PlayEffectSystem system = new PlayEffectSystem(eventManager, entityManager, blueprintManager);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        return system;
    }

    private long setupPlayer(EntityManager entityManager, EventManager eventManager, String playerId, String effectCard) {
        long playerEntityId = entityManager.createEntity();

        PlayerComponent playerComponent = new PlayerComponent();
        playerComponent.getHand().push(effectCard);
        entityManager.addComponent(playerEntityId, playerComponent);

        eventManager.queueEvent(EventType.PLAYER_ENTITY_CREATED, new PlayerEntityCreatedEvent(playerId, playerEntityId));

        return playerEntityId;
    }

    private long setupThreatPool(EntityManager entityManager, EventManager eventManager) {
        long threatPoolEntityId = entityManager.createEntity();
        entityManager.addComponent(threatPoolEntityId, new ThreatComponent());

        eventManager.queueEvent(EventType.THREAT_POOL_INITIALIZED, new ThreatPoolInitializedEvent(threatPoolEntityId));

        return threatPoolEntityId;
    }

    private long createTarget(EntityManager entityManager) {
        long targetEntityId = entityManager.createEntity();

        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);
        entityManager.addComponent(targetEntityId, new GameplayTagsComponent());

        return targetEntityId;
    }

    private void playEffect(EventManager eventManager, String playerId, String effectCard, long targetEntityId) {
        PlayEffectAction playEffectAction = new PlayEffectAction(effectCard, targetEntityId);
        playEffectAction.setPlayerId(playerId);

        eventManager.queueEvent(ActionType.PLAY_EFFECT, playEffectAction);
    }
}
