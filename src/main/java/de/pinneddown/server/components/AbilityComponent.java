package de.pinneddown.server.components;

import de.pinneddown.server.AbilityActivationType;
import de.pinneddown.server.EntityComponent;
import de.pinneddown.server.TargetType;

import java.util.ArrayList;

public class AbilityComponent implements EntityComponent {
    private String abilityActivationType;
    private ArrayList<String> abilityEffects;
    private ArrayList<String> requiredTags;
    private ArrayList<String> blockedTags;
    private String targetType;

    public AbilityComponent() {
        abilityEffects = new ArrayList<>();
        requiredTags = new ArrayList<>();
        blockedTags = new ArrayList<>();
    }

    public String getAbilityActivationType() {
        return abilityActivationType;
    }

    public void setAbilityActivationType(String abilityActivationType) {
        this.abilityActivationType = abilityActivationType;
    }

    public AbilityActivationType getActivationTypeEnum() {
        if ("Dominant".equals(abilityActivationType)) {
            return AbilityActivationType.DOMINANT;
        } else if ("Immediate".equals(abilityActivationType)) {
            return AbilityActivationType.IMMEDIATE;
        } else if ("Fight".equals(abilityActivationType)) {
            return AbilityActivationType.FIGHT;
        } else {
            return AbilityActivationType.PASSIVE;
        }
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

    public TargetType getTargetTypeEnum() {
        if ("AssignedTo".equals(targetType)) {
            return TargetType.ASSIGNED_TO;
        } else if ("Global".equals(targetType)) {
            return TargetType.GLOBAL;
        } else if ("Starship".equals(targetType)) {
            return TargetType.STARSHIP;
        } else {
            return TargetType.SELF;
        }
    }
}
