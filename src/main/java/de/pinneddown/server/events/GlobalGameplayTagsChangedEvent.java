package de.pinneddown.server.events;

import java.util.ArrayList;

public class GlobalGameplayTagsChangedEvent {
    private ArrayList<String> globalGameplayTags;

    public GlobalGameplayTagsChangedEvent() {
    }

    public GlobalGameplayTagsChangedEvent(ArrayList<String> globalGameplayTags) {
        this.globalGameplayTags = globalGameplayTags;
    }

    public ArrayList<String> getGlobalGameplayTags() {
        return globalGameplayTags;
    }

    public void setGlobalGameplayTags(ArrayList<String> globalGameplayTags) {
        this.globalGameplayTags = globalGameplayTags;
    }
}
