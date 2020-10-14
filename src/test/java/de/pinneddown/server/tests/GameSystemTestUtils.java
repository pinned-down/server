package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.AbilityEffectComponent;
import org.assertj.core.util.Lists;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameSystemTestUtils {
    public BlueprintManager createBlueprintManager(EntityManager entityManager) {
        return createBlueprintManager(entityManager, null);
    }

    public BlueprintManager createBlueprintManager(EntityManager entityManager, Blueprint singleBlueprint) {
        BlueprintSet blueprints = mock(BlueprintSet.class);

        if (singleBlueprint != null) {
            when(blueprints.getBlueprint(anyString())).thenReturn(singleBlueprint);
        }

        BlueprintManager blueprintManager = new BlueprintManager(entityManager);
        blueprintManager.setBlueprints(blueprints);

        return blueprintManager;
    }

    public long createIndefiniteEffect(EntityManager entityManager) {
        long entityId = entityManager.createEntity();
        AbilityEffectComponent abilityEffectComponent = new AbilityEffectComponent();
        abilityEffectComponent.setAbilityEffectDuration(AbilityEffectDuration.INDEFINITE.toString());
        entityManager.addComponent(entityId, abilityEffectComponent);
        return entityId;
    }
}
