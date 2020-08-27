package de.pinneddown.server.actions;

public class ResolveFightAction extends PlayerAction {
    private long entityId;

    public ResolveFightAction() {
    }

    public ResolveFightAction(long entityId) {
        this.entityId = entityId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }
}
