package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

import java.util.ArrayList;

public class TargetGameplayTagsConditionComponent implements EntityComponent {
    private ArrayList<String> targetBlockedTags;
    private ArrayList<String> targetRequiredTags;

    public TargetGameplayTagsConditionComponent() {
        targetBlockedTags = new ArrayList<>();
        targetRequiredTags = new ArrayList<>();
    }

    public ArrayList<String> getTargetBlockedTags() {
        return targetBlockedTags;
    }

    public void setTargetBlockedTags(ArrayList<String> targetBlockedTags) {
        this.targetBlockedTags = targetBlockedTags;
    }

    public ArrayList<String> getTargetRequiredTags() {
        return targetRequiredTags;
    }

    public void setTargetRequiredTags(ArrayList<String> targetRequiredTags) {
        this.targetRequiredTags = targetRequiredTags;
    }
}
