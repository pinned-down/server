package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

import java.util.ArrayList;

public class AssignedStarshipGameplayTagsConditionComponent implements EntityComponent {
    private ArrayList<String> assignedStarshipRequiredTags;
    private ArrayList<String> assignedStarshipBlockedTags;

    public AssignedStarshipGameplayTagsConditionComponent() {
        this.assignedStarshipRequiredTags = new ArrayList<>();
        this.assignedStarshipBlockedTags = new ArrayList<>();
    }

    public ArrayList<String> getAssignedStarshipRequiredTags() {
        return assignedStarshipRequiredTags;
    }

    public void setAssignedStarshipRequiredTags(ArrayList<String> assignedStarshipRequiredTags) {
        this.assignedStarshipRequiredTags = assignedStarshipRequiredTags;
    }

    public ArrayList<String> getAssignedStarshipBlockedTags() {
        return assignedStarshipBlockedTags;
    }

    public void setAssignedStarshipBlockedTags(ArrayList<String> assignedStarshipBlockedTags) {
        this.assignedStarshipBlockedTags = assignedStarshipBlockedTags;
    }
}
