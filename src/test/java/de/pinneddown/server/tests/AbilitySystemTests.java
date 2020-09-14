package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.actions.ActivateAbilityAction;
import de.pinneddown.server.components.AbilitiesComponent;
import de.pinneddown.server.components.AbilityComponent;
import de.pinneddown.server.components.PowerComponent;
import de.pinneddown.server.systems.AbilitySystem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbilitySystemTests extends GameSystemTestSuite {
    private static final String ABILITY_BLUEPRINT_ID = "testAbility";;
    private static final String EFFECT_BLUEPRINT_ID = "testEffect";

    @Test
    void appliesPowerEffect() {
        // ARRANGE
        EventManager eventManager = new EventManager();
        EntityManager entityManager = new EntityManager(eventManager);

        createSystem(entityManager, eventManager);

        // Setup card with ability.
        long entityId = entityManager.createEntity();

        ArrayList<String> abilities = new ArrayList<>();
        abilities.add(ABILITY_BLUEPRINT_ID);

        AbilitiesComponent abilitiesComponent = new AbilitiesComponent();
        abilitiesComponent.setAbilities(abilities);

        entityManager.addComponent(entityId, abilitiesComponent);

        // Create target.
        long targetEntityId = createTarget(entityManager);

        // ACT
        eventManager.queueEvent(ActionType.ACTIVATE_ABILITY,
                new ActivateAbilityAction(entityId, 0, targetEntityId));

        // ASSERT
        PowerComponent powerComponent = entityManager.getComponent(targetEntityId, PowerComponent.class);
        assertThat(powerComponent.getPowerModifier()).isGreaterThan(0);
    }

    private AbilitySystem createSystem(EntityManager entityManager, EventManager eventManager) {
        BlueprintManager blueprintManager = createBlueprintManager(entityManager);
        AbilitySystem system = new AbilitySystem(eventManager, entityManager, blueprintManager);

        return system;
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

        // Create blueprint manager.
        BlueprintSet blueprints = mock(BlueprintSet.class);

        when(blueprints.getBlueprint(ABILITY_BLUEPRINT_ID)).thenReturn(abilityBlueprint);
        when(blueprints.getBlueprint(EFFECT_BLUEPRINT_ID)).thenReturn(effectBlueprint);

        BlueprintManager blueprintManager = new BlueprintManager(entityManager);
        blueprintManager.setBlueprints(blueprints);

        return blueprintManager;
    }

    private long createTarget(EntityManager entityManager) {
        long targetEntityId = entityManager.createEntity();

        PowerComponent powerComponent = new PowerComponent();
        entityManager.addComponent(targetEntityId, powerComponent);

        return targetEntityId;
    }
}
