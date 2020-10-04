package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.AbilityEffectComponent;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameSystemTestSuite {
    protected BlueprintManager createMockBlueprintManager(EntityManager entityManager, Blueprint singleBlueprint) {
        BlueprintSet blueprints = mock(BlueprintSet.class);
        when(blueprints.getBlueprint(anyString())).thenReturn(singleBlueprint);

        BlueprintManager blueprintManager = new BlueprintManager(entityManager);
        blueprintManager.setBlueprints(blueprints);

        return blueprintManager;
    }

    protected long createIndefiniteEffect(EntityManager entityManager) {
        long entityId = entityManager.createEntity();
        AbilityEffectComponent abilityEffectComponent = new AbilityEffectComponent();
        abilityEffectComponent.setAbilityEffectDuration(AbilityEffectDuration.INDEFINITE.toString());
        entityManager.addComponent(entityId, abilityEffectComponent);
        return entityId;
    }
}
