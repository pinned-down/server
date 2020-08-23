package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

import java.util.ArrayList;

public class GameplayTagsComponent implements EntityComponent {
    private ArrayList<String> initialGameplayTags;

    public ArrayList<String> getInitialGameplayTags() {
        return initialGameplayTags;
    }

    public void setInitialGameplayTags(ArrayList<String> initialGameplayTags) {
        this.initialGameplayTags = initialGameplayTags;
    }
}
