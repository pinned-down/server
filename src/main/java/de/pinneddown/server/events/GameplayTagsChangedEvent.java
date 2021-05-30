package de.pinneddown.server.events;

import java.util.ArrayList;

public class GameplayTagsChangedEvent {
    private long entityId;
    private ArrayList<String> gameplayTags;

    public GameplayTagsChangedEvent() {
    }

    public GameplayTagsChangedEvent(long entityId, ArrayList<String> gameplayTags) {
        this.entityId = entityId;
        this.gameplayTags = new ArrayList<>(gameplayTags);
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public ArrayList<String> getGameplayTags() {
        return gameplayTags;
    }

    public void setGameplayTags(ArrayList<String> gameplayTags) {
        this.gameplayTags = new ArrayList<>(gameplayTags);
    }

    @Override
    public String toString() {
        return "GameplayTagsChangedEvent{" +
                "entityId=" + entityId +
                ", gameplayTags=" + gameplayTags +
                '}';
    }
}
