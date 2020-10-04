package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

import java.util.ArrayList;

public class AbilityComponent implements EntityComponent {
    private ArrayList<String> abilityEffects;
    private ArrayList<String> requiredTags;
    private ArrayList<String> blockedTags;

    private String targetType;

    public AbilityComponent() {
        abilityEffects = new ArrayList<>();
        requiredTags = new ArrayList<>();
        blockedTags = new ArrayList<>();
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

    public ArrayList<String> getBlockedTags() {
        return blockedTags;
    }

    public void setBlockedTags(ArrayList<String> blockedTags) {
        this.blockedTags = blockedTags;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}
