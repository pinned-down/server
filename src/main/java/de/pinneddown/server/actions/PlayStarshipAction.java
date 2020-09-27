package de.pinneddown.server.actions;

public class PlayStarshipAction extends PlayerAction {
    private String blueprintId;

    public PlayStarshipAction() {
    }

    public PlayStarshipAction(String blueprintId) {
        this.blueprintId = blueprintId;
    }

    public String getBlueprintId() {
        return blueprintId;
    }

    public void setBlueprintId(String blueprintId) {
        this.blueprintId = blueprintId;
    }
}
