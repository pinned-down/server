package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.events.StarshipDefeatedEvent;
import de.pinneddown.server.events.TurnPhaseStartedEvent;
import de.pinneddown.server.systems.AbilitySystem;
import de.pinneddown.server.util.GameplayTagUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class AbilitySystemTests {
    private static final String IMMEDIATE_ABILITY_BLUEPRINT_ID = "testImmediateAbility";
    private static final String PASSIVE_ABILITY_BLUEPRINT_ID = "testPassiveAbility";
    private static final String DOMINANT_ABILITY_BLUEPRINT_ID = "testDominantAbility";
    private static final String FIGHT_ABILITY_BLUEPRINT_ID = "testFightAbility";

    private static final String ABILITY_WITH_REQUIRED_TAG_BLUEPRINT_ID = "testAbilityWithRequiredTag";
    private static final String ABILITY_WITH_BLOCKED_TAG_BLUEPRINT_ID = "testAbilityWithBlockedTag";

    private static final String TEST_GAMEPLAY_TAG = "testTag";

    private boolean abilityEffectApplied;

    @Test
    void activatesImmediateAbilities() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(IMMEDIATE_ABILITY_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);
        entityManager.addComponent(entityId, abilitiesComponent);

        // Register for events.
        abilityEffectApplied = false;
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);

        // ACT
        eventManager.queueEvent(EventType.CARD_PLAYED,
                new CardPlayedEvent(entityId, null, EntityManager.INVALID_ENTITY, EntityManager.INVALID_ENTITY));

        // ASSERT
        assertThat(abilityEffectApplied).isTrue();
    }

    @Test
    void activatesPassiveAbilities() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(PASSIVE_ABILITY_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);
        entityManager.addComponent(entityId, abilitiesComponent);

        // Register for events.
        abilityEffectApplied = false;
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);

        // ACT
        eventManager.queueEvent(EventType.CARD_PLAYED,
                new CardPlayedEvent(entityId, null, EntityManager.INVALID_ENTITY, EntityManager.INVALID_ENTITY));

        // ASSERT
        assertThat(abilityEffectApplied).isTrue();
    }

    @Test
    void activatesDominantAbilities() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(DOMINANT_ABILITY_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);
        entityManager.addComponent(entityId, abilitiesComponent);

        // Register for events.
        abilityEffectApplied = false;
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);

        // ACT
        eventManager.queueEvent(EventType.STARSHIP_DEFEATED, new StarshipDefeatedEvent(0L, entityId));

        // ASSERT
        assertThat(abilityEffectApplied).isTrue();
    }

    @Test
    void activatesFightAbilities() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(FIGHT_ABILITY_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);
        entityManager.addComponent(entityId, abilitiesComponent);

        eventManager.queueEvent(EventType.CARD_PLAYED,
                new CardPlayedEvent(entityId, null, EntityManager.INVALID_ENTITY, EntityManager.INVALID_ENTITY));

        // Register for events.
        abilityEffectApplied = false;
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);

        // ACT
        eventManager.queueEvent(EventType.TURN_PHASE_STARTED, new TurnPhaseStartedEvent(TurnPhase.FIGHT));

        // ASSERT
        assertThat(abilityEffectApplied).isTrue();
    }

    @Test
    void doesNotActivateAbilitiesForTargetWithoutRequiredTags() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(ABILITY_WITH_REQUIRED_TAG_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);
        entityManager.addComponent(entityId, abilitiesComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        // Register for events.
        abilityEffectApplied = false;
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);

        // ACT
        eventManager.queueEvent(EventType.CARD_PLAYED,
                new CardPlayedEvent(entityId, null, 0, targetEntityId));

        // ASSERT
        assertThat(abilityEffectApplied).isFalse();
    }

    @Test
    void doesNotActivateAbilitiesForTargetWithBlockedTags() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(ABILITY_WITH_BLOCKED_TAG_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);
        entityManager.addComponent(entityId, abilitiesComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();
        GameplayTagsComponent gameplayTagsComponent = new GameplayTagsComponent();
        gameplayTagsComponent.setInitialGameplayTags(Lists.newArrayList(TEST_GAMEPLAY_TAG));
        entityManager.addComponent(targetEntityId, gameplayTagsComponent);

        // Register for events.
        abilityEffectApplied = false;
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);

        // ACT
        eventManager.queueEvent(EventType.CARD_PLAYED,
                new CardPlayedEvent(entityId, null, EntityManager.INVALID_ENTITY, targetEntityId));

        // ASSERT
        assertThat(abilityEffectApplied).isFalse();
    }

    private AbilitySystem createSystem(EntityManager entityManager, EventManager eventManager) {
        BlueprintManager blueprintManager = createBlueprintManager(entityManager);
        GameplayTagUtils gameplayTagUtils = new GameplayTagUtils(eventManager, entityManager);

        AbilitySystem system = new AbilitySystem(eventManager, entityManager, blueprintManager, gameplayTagUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        return system;
    }

    private BlueprintManager createBlueprintManager(EntityManager entityManager) {
        // Create effecs.
        Blueprint effectBlueprint = new Blueprint("testEffect");
        effectBlueprint.getComponents().add(PowerComponent.class.getSimpleName());
        effectBlueprint.getAttributes().put("PowerModifier", 1);

        // Create ability.
        ArrayList<String> effects = new ArrayList<>();
        effects.add(effectBlueprint.getId());

        // Create immediate ability.
        Blueprint immediateAbilityBlueprint = new Blueprint(IMMEDIATE_ABILITY_BLUEPRINT_ID);
        immediateAbilityBlueprint.getComponents().add(AbilityComponent.class.getSimpleName());
        immediateAbilityBlueprint.getAttributes().put("AbilityEffects", effects);
        immediateAbilityBlueprint.getAttributes().put("AbilityActivationType", "Immediate");

        // Create passive ability.
        Blueprint passiveAbilityBlueprint = new Blueprint(PASSIVE_ABILITY_BLUEPRINT_ID);
        passiveAbilityBlueprint.getComponents().add(AbilityComponent.class.getSimpleName());
        passiveAbilityBlueprint.getAttributes().put("AbilityEffects", effects);
        passiveAbilityBlueprint.getAttributes().put("AbilityActivationType", "Passive");

        // Create dominant ability.
        Blueprint dominantAbilityBlueprint = new Blueprint(DOMINANT_ABILITY_BLUEPRINT_ID);
        dominantAbilityBlueprint.getComponents().add(AbilityComponent.class.getSimpleName());
        dominantAbilityBlueprint.getAttributes().put("AbilityEffects", effects);
        dominantAbilityBlueprint.getAttributes().put("AbilityActivationType", "Dominant");

        // Create fight ability.
        Blueprint fightAbilityBlueprint = new Blueprint(FIGHT_ABILITY_BLUEPRINT_ID);
        fightAbilityBlueprint.getComponents().add(AbilityComponent.class.getSimpleName());
        fightAbilityBlueprint.getAttributes().put("AbilityEffects", effects);
        fightAbilityBlueprint.getAttributes().put("AbilityActivationType", "Fight");

        // Create ability with required tag.
        Blueprint abilityWithRequiredTagBlueprint = new Blueprint(ABILITY_WITH_REQUIRED_TAG_BLUEPRINT_ID);
        abilityWithRequiredTagBlueprint.getComponents().add(AbilityComponent.class.getSimpleName());
        abilityWithRequiredTagBlueprint.getComponents().add(TargetGameplayTagsConditionComponent.class.getSimpleName());
        abilityWithRequiredTagBlueprint.getAttributes().put("AbilityEffects", effects);
        abilityWithRequiredTagBlueprint.getAttributes().put("AbilityActivationType", "Immediate");
        abilityWithRequiredTagBlueprint.getAttributes().put("TargetRequiredTags", Lists.newArrayList(TEST_GAMEPLAY_TAG));
        abilityWithRequiredTagBlueprint.getAttributes().put("TargetBlockedTags", new ArrayList<String>());

        // Create ability with blocked tag.
        Blueprint abilityWithBlockedTagBlueprint = new Blueprint(ABILITY_WITH_BLOCKED_TAG_BLUEPRINT_ID);
        abilityWithBlockedTagBlueprint.getComponents().add(AbilityComponent.class.getSimpleName());
        abilityWithBlockedTagBlueprint.getComponents().add(TargetGameplayTagsConditionComponent.class.getSimpleName());
        abilityWithBlockedTagBlueprint.getAttributes().put("AbilityEffects", effects);
        abilityWithBlockedTagBlueprint.getAttributes().put("AbilityActivationType", "Immediate");
        abilityWithBlockedTagBlueprint.getAttributes().put("TargetRequiredTags", new ArrayList<String>());
        abilityWithBlockedTagBlueprint.getAttributes().put("TargetBlockedTags", Lists.newArrayList(TEST_GAMEPLAY_TAG));

        // Create blueprint manager.
        ArrayList<Blueprint> blueprints = new ArrayList<>();
        blueprints.add(immediateAbilityBlueprint);
        blueprints.add(passiveAbilityBlueprint);
        blueprints.add(dominantAbilityBlueprint);
        blueprints.add(fightAbilityBlueprint);
        blueprints.add(abilityWithRequiredTagBlueprint);
        blueprints.add(abilityWithBlockedTagBlueprint);
        blueprints.add(effectBlueprint);

        BlueprintSet blueprintSet = new BlueprintSet();
        blueprintSet.setBlueprints(blueprints);

        BlueprintManager blueprintManager = new BlueprintManager(entityManager);
        blueprintManager.setBlueprints(blueprintSet);

        return blueprintManager;
    }

    private void onAbilityEffectApplied(GameEvent gameEvent) {
        abilityEffectApplied = true;
    }
}
