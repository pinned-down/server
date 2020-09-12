package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

import java.util.ArrayList;

public class AbilityComponent implements EntityComponent {
    private ArrayList<String> abilityEffects;
    private ArrayList<String> requiredTags;
    private ArrayList<String> targetBlockedTags;
    private ArrayList<String> targetRequiredTags;

    public AbilityComponent() {
        abilityEffects = new ArrayList<>();
        requiredTags = new ArrayList<>();
        targetBlockedTags = new ArrayList<>();
        targetRequiredTags = new ArrayList<>();
    }

    public ArrayList<String> getAbilityEffects() {
        return abilityEffects;
    }

    public void setAbilityEffects(ArrayList<String> abilityEffects) {
        this.abilityEffects = abilityEffects;
    }

    public ArrayList<String> getRequiredTags() {
        return requiredTags;
    }

    public void setRequiredTags(ArrayList<String> requiredTags) {
        this.requiredTags = requiredTags;
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
