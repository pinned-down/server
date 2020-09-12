package de.pinneddown.server.actions;

public class PlayEffectAction extends PlayerAction {
    private String blueprintId;
    private long targetEntityId;

    public PlayEffectAction() {
    }

    public PlayEffectAction(String blueprintId, long targetEntityId) {
        this.blueprintId = blueprintId;
        this.targetEntityId = targetEntityId;
    }

    public String getBlueprintId() {
        return blueprintId;
    }

    public void setBlueprintId(String blueprintId) {
        this.blueprintId = blueprintId;
    }

    public long getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(long targetEntityId) {
        this.targetEntityId = targetEntityId;
    }
}
