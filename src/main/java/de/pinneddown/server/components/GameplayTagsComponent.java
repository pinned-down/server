package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

import java.util.ArrayList;

public class GameplayTagsComponent implements EntityComponent {
    private ArrayList<String> initialGameplayTags;
    private ArrayList<String> globalGameplayTags;
    private ArrayList<String> temporaryGameplayTags;

    public GameplayTagsComponent() {
        initialGameplayTags = new ArrayList<>();
        globalGameplayTags = new ArrayList<>();
        temporaryGameplayTags = new ArrayList<>();
    }

    public ArrayList<String> getInitialGameplayTags() {
        return initialGameplayTags;
    }

    public void setInitialGameplayTags(ArrayList<String> initialGameplayTags) {
        this.initialGameplayTags = initialGameplayTags;
    }

    public ArrayList<String> getGlobalGameplayTags() {
        return globalGameplayTags;
    }

    public void setGlobalGameplayTags(ArrayList<String> globalGameplayTags) {
        this.globalGameplayTags = globalGameplayTags;
    }

    public ArrayList<String> getTemporaryGameplayTags() {
        return temporaryGameplayTags;
    }

    public void setTemporaryGameplayTags(ArrayList<String> temporaryGameplayTags) {
        this.temporaryGameplayTags = temporaryGameplayTags;
    }
}
