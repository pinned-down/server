package de.pinneddown.server.tests;

import de.pinneddown.server.Blueprint;
import de.pinneddown.server.BlueprintManager;
import de.pinneddown.server.BlueprintSet;
import de.pinneddown.server.EntityManager;

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
}
