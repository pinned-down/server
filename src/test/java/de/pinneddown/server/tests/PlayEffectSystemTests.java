package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.PlayEffectAction;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.components.ThreatComponent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import de.pinneddown.server.events.ThreatModifiersChangedEvent;
import de.pinneddown.server.events.ThreatPoolInitializedEvent;
import de.pinneddown.server.systems.PlayEffectSystem;
import de.pinneddown.server.util.GameplayTagUtils;
import de.pinneddown.server.util.PlayerUtils;
import de.pinneddown.server.util.ThreatUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayEffectSystemTests {
    private static final String EFFECT_CARD_BLUEPRINT_ID = "testEffectCard";
    private static final int EFFECT_CARD_THREAT = 2;

    @Test
    void removesCardFromHand() {
        // ARRANGE
        // Create system.
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

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
        assertThat(playerComponent.getDiscardPile().getCards()).contains(EFFECT_CARD_BLUEPRINT_ID);
    }

    @Test
    void increasesThreat() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

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
    void considersThreatModifiers() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

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

        // Add threat modifiers.
        int threatModifier = -1;

        HashMap<String, Integer> threatModifiers = new HashMap<>();
        threatModifiers.put(EFFECT_CARD_BLUEPRINT_ID, threatModifier);

        eventManager.queueEvent(EventType.THREAT_MODIFIERS_CHANGED, new ThreatModifiersChangedEvent(threatModifiers));

        // ACT
        playEffect(eventManager, playerId, EFFECT_CARD_BLUEPRINT_ID, targetEntityId);

        // ASSERT
        int newThreat = threatComponent.getThreat();
        assertThat(newThreat).isEqualTo(oldThreat + EFFECT_CARD_THREAT + threatModifier);
    }

    private BlueprintManager createBlueprintManager(EntityManager entityManager) {
        Blueprint effectCardBlueprint = new Blueprint();
        effectCardBlueprint.getComponents().add(ThreatComponent.class.getSimpleName());
        effectCardBlueprint.getAttributes().put("Threat", EFFECT_CARD_THREAT);

        // Create blueprint manager.
        GameSystemTestUtils testUtils = new GameSystemTestUtils();
        return testUtils.createBlueprintManager(entityManager, effectCardBlueprint);
    }

    private PlayEffectSystem createSystem(EntityManager entityManager, EventManager eventManager) {
        BlueprintManager blueprintManager = createBlueprintManager(entityManager);
        ThreatUtils threatUtils = new ThreatUtils(eventManager, entityManager);
        PlayerUtils playerUtils = new PlayerUtils(eventManager, entityManager, blueprintManager, threatUtils);

        GameplayTagUtils gameplayTagUtils = new GameplayTagUtils(eventManager, entityManager);

        PlayEffectSystem system = new PlayEffectSystem(eventManager, entityManager, blueprintManager, playerUtils,
                gameplayTagUtils);

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
        return targetEntityId;
    }

    private void playEffect(EventManager eventManager, String playerId, String effectCard, long targetEntityId) {
        PlayEffectAction playEffectAction = new PlayEffectAction(effectCard, targetEntityId);
        playEffectAction.setPlayerId(playerId);

        eventManager.queueEvent(ActionType.PLAY_EFFECT, playEffectAction);
    }
}
