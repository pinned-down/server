package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ActivateAbilityAction;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.CardPlayedEvent;
import de.pinneddown.server.systems.AbilitySystem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbilitySystemTests extends GameSystemTestSuite {
    private static final String ABILITY_BLUEPRINT_ID = "testAbility";
    private static final String PASSIVE_ABILITY_BLUEPRINT_ID = "testPassiveAbility";
    private static final String EFFECT_BLUEPRINT_ID = "testEffect";
    private static final String OVERLOAD_EFFECT_BLUEPRINT_ID = "overloadEffect";

    private boolean overloaded;
    private boolean abilityEffectApplied;

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

        // Register for events.
        abilityEffectApplied = false;
        eventManager.addEventHandler(EventType.ABILITY_EFFECT_APPLIED, this::onAbilityEffectApplied);

        // ACT
        eventManager.queueEvent(EventType.CARD_PLAYED,
                new CardPlayedEvent(entityId, null, 0));

        // ASSERT
        assertThat(abilityEffectApplied).isTrue();
    }

    private AbilitySystem createSystem(EntityManager entityManager, EventManager eventManager, String effect) {
        BlueprintManager blueprintManager = createBlueprintManager(entityManager, effect);

        AbilitySystem system = new AbilitySystem(eventManager, entityManager, blueprintManager);

        eventManager.queueEvent(EventType.READY_TO_START, null);

        return system;
    }

    private BlueprintManager createBlueprintManager(EntityManager entityManager, String effect) {
        // Create effecs.
        Blueprint effectBlueprint = new Blueprint();
        effectBlueprint.getComponents().add(PowerComponent.class.getSimpleName());
        effectBlueprint.getAttributes().put("PowerModifier", 1);

        Blueprint overloadEffectBlueprint = new Blueprint();
        overloadEffectBlueprint.getComponents().add(OverloadComponent.class.getSimpleName());
        overloadEffectBlueprint.getAttributes().put("Overloads", 1);

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
        when(blueprints.getBlueprint(OVERLOAD_EFFECT_BLUEPRINT_ID)).thenReturn(overloadEffectBlueprint);

        BlueprintManager blueprintManager = new BlueprintManager(entityManager);
        blueprintManager.setBlueprints(blueprints);

        return blueprintManager;
    }

    private void onStarshipOverloaded(GameEvent gameEvent) {
        overloaded = true;
    }

    private void onAbilityEffectApplied(GameEvent gameEvent) {
        abilityEffectApplied = true;
    }
}
