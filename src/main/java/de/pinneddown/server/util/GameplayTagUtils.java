package de.pinneddown.server.util;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.GameEvent;
import de.pinneddown.server.components.GameplayTagsComponent;
import de.pinneddown.server.events.GlobalGameplayTagsChangedEvent;
import de.pinneddown.server.events.GlobalGameplayTagsInitializedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class GameplayTagUtils {
    private EventManager eventManager;
    private EntityManager entityManager;

    private long globalGameplayTagsEntityId;

    public GameplayTagUtils(EventManager eventManager, EntityManager entityManager) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;

        this.eventManager.addEventHandler(EventType.GLOBAL_GAMEPLAY_TAGS_INITIALIZED,
                this::onGlobalGameplayTagsInitialized);
    }

    public ArrayList<String> getGameplayTags(long entityId) {
        ArrayList<String> combinedTags = new ArrayList<>();

        // Add global tags.
        GameplayTagsComponent globalGameplayTagsComponent =
                entityManager.getComponent(globalGameplayTagsEntityId, GameplayTagsComponent.class);

        if (globalGameplayTagsComponent != null) {
            combinedTags.addAll(globalGameplayTagsComponent.getGlobalGameplayTags());
        }

        // Add entity tags.
        GameplayTagsComponent entityGameplayTagsComponent =
                entityManager.getComponent(entityId, GameplayTagsComponent.class);

        if (entityGameplayTagsComponent != null) {
            combinedTags.addAll(entityGameplayTagsComponent.getInitialGameplayTags());
            combinedTags.addAll(entityGameplayTagsComponent.getTemporaryGameplayTags());
        }

        return combinedTags;
    }

    public void addGameplayTag(ArrayList<String> gameplayTags, String gameplayTag) {
        if (gameplayTags.contains(gameplayTag)) {
            return;
        }

        gameplayTags.add(gameplayTag);
    }

    public void removeGameplayTag(ArrayList<String> gameplayTags, String gameplayTag) {
        for (int index = gameplayTags.size() - 1; index >= 0; --index) {
            String currentTag = gameplayTags.get(index);

            if (currentTag.equals(gameplayTag) || currentTag.startsWith(gameplayTag + ".")) {
                gameplayTags.remove(index);
            }
        }
    }

    public void addGlobalGameplayTag(String gameplayTag) {
        GameplayTagsComponent globalGameplayTagsComponent =
                entityManager.getComponent(globalGameplayTagsEntityId, GameplayTagsComponent.class);
        addGameplayTag(globalGameplayTagsComponent.getGlobalGameplayTags(), gameplayTag);

        // Notify listeners.
        onGlobalGameplayTagsChanged();
    }

    public void removeGlobalGameplayTag(String gameplayTag) {
        GameplayTagsComponent globalGameplayTagsComponent =
                entityManager.getComponent(globalGameplayTagsEntityId, GameplayTagsComponent.class);
        removeGameplayTag(globalGameplayTagsComponent.getGlobalGameplayTags(), gameplayTag);

        // Notify listeners.
        onGlobalGameplayTagsChanged();
    }

    private void onGlobalGameplayTagsInitialized(GameEvent gameEvent) {
        GlobalGameplayTagsInitializedEvent eventData = (GlobalGameplayTagsInitializedEvent)gameEvent.getEventData();
        globalGameplayTagsEntityId = eventData.getEntityId();
    }

    private void onGlobalGameplayTagsChanged() {
        GameplayTagsComponent globalGameplayTagsComponent =
                entityManager.getComponent(globalGameplayTagsEntityId, GameplayTagsComponent.class);
        eventManager.queueEvent(EventType.GLOBAL_GAMEPLAY_TAGS_CHANGED,
                new GlobalGameplayTagsChangedEvent(globalGameplayTagsComponent.getGlobalGameplayTags()));
    }
}
