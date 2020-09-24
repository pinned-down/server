package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ActivateAbilityAction;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.AbilityEffectRemovedEvent;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.systems.AbilitySystem;
import de.pinneddown.server.util.PowerUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbilitySystemTests extends GameSystemTestSuite {
    private static final String ABILITY_BLUEPRINT_ID = "testAbility";
    private static final String PASSIVE_ABILITY_BLUEPRINT_ID = "testPassiveAbility";
    private static final String EFFECT_BLUEPRINT_ID = "testEffect";
    private static final String POWER_PER_LOCATION_EFFECT_BLUEPRINT_ID = "powerPerLocationEffect";
    private static final String OVERLOAD_EFFECT_BLUEPRINT_ID = "overloadEffect";
    private static final String POWER_DIFFERENCE_CONDITION_EFFECT_BLUEPRINT_ID = "powerDifferenceConditionEffect";

    private boolean overloaded;

    @Test
    void appliesPowerEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager, EFFECT_BLUEPRINT_ID);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(ABILITY_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);

        entityManager.addComponent(entityId, abilitiesComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        // ACT
        eventManager.queueEvent(ActionType.ACTIVATE_ABILITY,
                new ActivateAbilityAction(entityId, 0, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isGreaterThan(0);
    }

    @Test
    void appliesPowerPerLocationEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager, POWER_PER_LOCATION_EFFECT_BLUEPRINT_ID);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(ABILITY_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);

        entityManager.addComponent(entityId, abilitiesComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        eventManager.queueEvent(EventType.CURRENT_LOCATION_CHANGED, null);

        // ACT
        eventManager.queueEvent(ActionType.ACTIVATE_ABILITY,
                new ActivateAbilityAction(entityId, 0, targetEntityId));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isGreaterThan(0);
    }

    @Test
    void removesPowerEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager, EFFECT_BLUEPRINT_ID);

        // Create effect.
        long effectEntityId = entityManager.createEntity();
        PowerComponent effectPowerComponent = new PowerComponent();
        effectPowerComponent.setPowerModifier(2);

        entityManager.addComponent(effectEntityId, effectPowerComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        PowerComponent targetPowerComponent = new PowerComponent();
        targetPowerComponent.setPowerModifier(effectPowerComponent.getPowerModifier());

        entityManager.addComponent(targetEntityId, targetPowerComponent);

        // ACT
        eventManager.queueEvent(EventType.ABILITY_EFFECT_REMOVED,
                new AbilityEffectRemovedEvent(effectEntityId, targetEntityId));

        // ASSERT
        assertThat(targetPowerComponent.getPowerModifier()).isZero();
    }

    @Test
    void appliesOverloadEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager, OVERLOAD_EFFECT_BLUEPRINT_ID);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(ABILITY_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);

        entityManager.addComponent(entityId, abilitiesComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        StructureComponent structureComponent = new StructureComponent();
        entityManager.addComponent(targetEntityId, structureComponent);

        // Listen for events.
        overloaded = false;
        eventManager.addEventHandler(EventType.STARSHIP_OVERLOADED, this::onStarshipOverloaded);

        // ACT
        eventManager.queueEvent(ActionType.ACTIVATE_ABILITY,
                new ActivateAbilityAction(entityId, 0, targetEntityId));

        // ASSERT
        assertThat(overloaded).isTrue();
    }

    @Test
    void applyEffectIfPowerDifferenceConditionFulfilled() {
        int powerModifier = applyEffectWithPowerDifferenceConditionAndReturnPowerModifier(2, 1);

        // ASSERT
        assertThat(powerModifier).isGreaterThan(0);
    }

    @Test
    void doesNotApplyEffectIfPowerDifferenceConditionNotFulfilled() {
        int powerModifier = applyEffectWithPowerDifferenceConditionAndReturnPowerModifier(1, 2);

        // ASSERT
        assertThat(powerModifier).isZero();
    }

    int applyEffectWithPowerDifferenceConditionAndReturnPowerModifier(int targetPower, int assignedToPower) {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager, POWER_DIFFERENCE_CONDITION_EFFECT_BLUEPRINT_ID);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(ABILITY_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);

        entityManager.addComponent(entityId, abilitiesComponent);

        // Create target.
        long targetEntityId = entityManager.createEntity();

        PowerComponent powerComponent = new PowerComponent();
        powerComponent.setBasePower(targetPower);
        entityManager.addComponent(targetEntityId, powerComponent);

        // Create assigned starship.
        long assignedTo = entityManager.createEntity();

        PowerComponent assignedToPowerComponent = new PowerComponent();
        assignedToPowerComponent.setBasePower(assignedToPower);
        entityManager.addComponent(assignedTo, assignedToPowerComponent);

        AssignmentComponent assignmentComponent = new AssignmentComponent();
        assignmentComponent.setAssignedTo(assignedTo);
        entityManager.addComponent(targetEntityId, assignmentComponent);

        // ACT
        eventManager.queueEvent(ActionType.ACTIVATE_ABILITY,
                new ActivateAbilityAction(entityId, 0, targetEntityId));

        return powerComponent.getPowerModifier();
    }

    @Test
    void activatesPassiveAbilities() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager, EFFECT_BLUEPRINT_ID);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(PASSIVE_ABILITY_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);
        entityManager.addComponent(entityId, abilitiesComponent);

        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(entityId, powerComponent);

        // ACT
        eventManager.queueEvent(EventType.CARD_PLAYED,
                new CardPlayedEvent(entityId, null, 0));

        // ASSERT
        assertThat(powerComponent.getPowerModifier()).isGreaterThan(0);
    }

    private AbilitySystem createSystem(EntityManager entityManager, EventManager eventManager, String effect) {
        BlueprintManager blueprintManager = createBlueprintManager(entityManager, effect);
        PowerUtils powerUtils = new PowerUtils(eventManager, entityManager);

        AbilitySystem system = new AbilitySystem(eventManager, entityManager, blueprintManager, powerUtils);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        return system;
    }

    private BlueprintManager createBlueprintManager(EntityManager entityManager, String effect) {
        // Create effecs.
        Blueprint effectBlueprint = new Blueprint();
        effectBlueprint.getComponents().add(PowerComponent.class.getSimpleName());
        effectBlueprint.getAttributes().put("PowerModifier", 1);

        Blueprint powerPerLocationEffectBlueprint = new Blueprint();
        powerPerLocationEffectBlueprint.getComponents().add(PowerPerLocationComponent.class.getSimpleName());
        powerPerLocationEffectBlueprint.getAttributes().put("PowerPerLocation", 1);

        Blueprint overloadEffectBlueprint = new Blueprint();
        overloadEffectBlueprint.getComponents().add(OverloadComponent.class.getSimpleName());
        overloadEffectBlueprint.getAttributes().put("Overloads", 1);

        Blueprint powerDifferenceConditionEffect = new Blueprint();
        powerDifferenceConditionEffect.getComponents().add(PowerDifferenceConditionComponent.class.getSimpleName());
        powerDifferenceConditionEffect.getAttributes().put("RequiredPowerDifference", 1);
        powerDifferenceConditionEffect.getComponents().add(PowerComponent.class.getSimpleName());
        powerDifferenceConditionEffect.getAttributes().put("PowerModifier", 1);

        // Create ability.
        ArrayList<String> effects = new ArrayList<>();
        effects.add(effect);

        Blueprint abilityBlueprint = new Blueprint();
        abilityBlueprint.getComponents().add(AbilityComponent.class.getSimpleName());
        abilityBlueprint.getAttributes().put("AbilityEffects", effects);

        // Create passive ability.
        Blueprint passiveAbilityBlueprint = new Blueprint();
        passiveAbilityBlueprint.getComponents().add(AbilityComponent.class.getSimpleName());
        passiveAbilityBlueprint.getAttributes().put("AbilityEffects", effects);
        passiveAbilityBlueprint.getAttributes().put("TargetType", TargetType.PASSIVE);

        // Create blueprint manager.
        BlueprintSet blueprints = mock(BlueprintSet.class);

        when(blueprints.getBlueprint(ABILITY_BLUEPRINT_ID)).thenReturn(abilityBlueprint);
        when(blueprints.getBlueprint(PASSIVE_ABILITY_BLUEPRINT_ID)).thenReturn(passiveAbilityBlueprint);
        when(blueprints.getBlueprint(EFFECT_BLUEPRINT_ID)).thenReturn(effectBlueprint);
        when(blueprints.getBlueprint(POWER_PER_LOCATION_EFFECT_BLUEPRINT_ID)).thenReturn(powerPerLocationEffectBlueprint);
        when(blueprints.getBlueprint(OVERLOAD_EFFECT_BLUEPRINT_ID)).thenReturn(overloadEffectBlueprint);
        when(blueprints.getBlueprint(POWER_DIFFERENCE_CONDITION_EFFECT_BLUEPRINT_ID)).thenReturn(powerDifferenceConditionEffect);

        BlueprintManager blueprintManager = new BlueprintManager(entityManager);
        blueprintManager.setBlueprints(blueprints);

        return blueprintManager;
    }

    private void onStarshipOverloaded(GameEvent gameEvent) {
        overloaded = true;
    }
}