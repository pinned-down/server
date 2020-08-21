package de.pinneddown.server.tests;

import de.pinneddown.server.*;
import de.pinneddown.server.components.OwnerComponent;
import de.pinneddown.server.components.PlayerComponent;
import de.pinneddown.server.events.PlayerEntityCreatedEvent;
import de.pinneddown.server.systems.FlagshipSystem;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class FlagshipSystemTests extends GameSystemTestSuite {
    @Test
    void playersPutFlagshipsIntoPlay() throws IOException {
        // ARRANGE
        EventManager eventManager = new EventManager();
        PlayerManager playerManager = new PlayerManager();
        EntityManager entityManager = new EntityManager();

        Blueprint flagshipBlueprint = new Blueprint();
        flagshipBlueprint.getComponents().add(OwnerComponent.class.getSimpleName());

        BlueprintManager blueprintManager = createMockBlueprintManager(entityManager, flagshipBlueprint);

        FlagshipSystem system = new FlagshipSystem(eventManager, playerManager, entityManager, blueprintManager);

        // ACT
        long playerEntityId = entityManager.createEntity();
        entityManager.addComponent(playerEntityId, new PlayerComponent());

        PlayerEntityCreatedEvent eventData = new PlayerEntityCreatedEvent();
        eventData.setEntityId(playerEntityId);

        eventManager.queueEvent(EventType.PLAYER_ENTITY_CREATED, eventData);

        // ASSERT
        long flagshipEntityId = playerEntityId + 1;

        OwnerComponent ownerComponent = entityManager.getComponent(flagshipEntityId, OwnerComponent.class);

        assertThat(ownerComponent).isNotNull();
        assertThat(ownerComponent.getOwner()).isEqualTo(playerEntityId);
    }
}