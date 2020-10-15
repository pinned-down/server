package de.pinneddown.server.util;

import de.pinneddown.server.EntityManager;
import de.pinneddown.server.EventManager;
import de.pinneddown.server.EventType;
import de.pinneddown.server.GameEvent;
import de.pinneddown.server.components.AbilityComponent;
import de.pinneddown.server.components.GameplayTagsComponent;
import de.pinneddown.server.events.GameplayTagsChangedEvent;
import de.pinneddown.server.events.GlobalGameplayTagsChangedEvent;
import de.pinneddown.server.events.GlobalGameplayTagsInitializedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
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
            addGameplayTagsUnique(combinedTags, globalGameplayTagsComponent.getGlobalGameplayTags());
        }

        // Add entity tags.
        GameplayTagsComponent entityGameplayTagsComponent =
                entityManager.getComponent(entityId, GameplayTagsComponent.class);

        if (entityGameplayTagsComponent != null) {
            addGameplayTagsUnique(combinedTags, entityGameplayTagsComponent.getInitialGameplayTags());
            addGameplayTagsUnique(combinedTags, entityGameplayTagsComponent.getTemporaryGameplayTags());
        }

        return combinedTags;
    }

    public void addGameplayTag(ArrayList<String> gameplayTags, String gameplayTag) {
        gameplayTags.add(gameplayTag);
    }

    public void addGameplayTagUnique(long entityId, String gameplayTag) {
        GameplayTagsComponent entityGameplayTagsComponent =
                entityManager.getComponent(entityId, GameplayTagsComponent.class);

        if (entityGameplayTagsComponent == null) {
            return;
        }

        if (!addGameplayTagUnique(entityGameplayTagsComponent.getTemporaryGameplayTags(), gameplayTag)) {
            return;
        }

        // Notify listeners.
        ArrayList<String> gameplayTags = getGameplayTags(entityId);
        eventManager.queueEvent(EventType.GAMEPLAY_TAGS_CHANGED, new GameplayTagsChangedEvent(entityId, gameplayTags));
    }

    public boolean addGameplayTagUnique(ArrayList<String> gameplayTags, String gameplayTag) {
        if (gameplayTags.contains(gameplayTag)) {
            return false;
        }

        gameplayTags.add(gameplayTag);
        return true;
    }

    public void addGameplayTagsUnique(ArrayList<String> gameplayTags, ArrayList<String> newTags) {
        for (String newTag : newTags) {
            addGameplayTagUnique(gameplayTags, newTag);
        }
    }

    public void removeGameplayTag(ArrayList<String> gameplayTags, String gameplayTag) {
        for (int index = 0; index < gameplayTags.size(); ++index) {
            String currentTag = gameplayTags.get(index);

            if (currentTag.equals(gameplayTag) || currentTag.startsWith(gameplayTag + ".")) {
                gameplayTags.remove(index);
                return;
            }
        }
    }

    public boolean hasGameplayTag(long entityId, String gameplayTag) {
        ArrayList<String> gameplayTags = getGameplayTags(entityId);
        return gameplayTags.contains(gameplayTag);
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

    public ArrayList<String> combineGameplayTags(ArrayList<String> first, ArrayList<String> second) {
        ArrayList<String> combined = new ArrayList<>();
        addGameplayTagsUnique(combined, first);
        addGameplayTagsUnique(combined, second);
        return combined;
    }

    public boolean matchesTagRequirements(long entityId, ArrayList<String> requiredTags,
                                          ArrayList<String> blockedTags) {
        ArrayList<String> gameplayTags = getGameplayTags(entityId);
        return matchesTagRequirements(gameplayTags, requiredTags, blockedTags);
    }

    public boolean matchesTagRequirements(ArrayList<String> gameplayTags, ArrayList<String> requiredTags,
                                          ArrayList<String> blockedTags) {
        return gameplayTags.containsAll(requiredTags) &&
                gameplayTags.stream().noneMatch(tag -> blockedTags.contains(tag));
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
