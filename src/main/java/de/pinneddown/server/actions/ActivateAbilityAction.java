package de.pinneddown.server.actions;

public class ActivateAbilityAction {
    private long entityId;
    private int abilityIndex;
    private long targetEntityId;

    public ActivateAbilityAction() {
    }

    public ActivateAbilityAction(long entityId, int abilityIndex, long targetEntityId) {
        this.entityId = entityId;
        this.abilityIndex = abilityIndex;
        this.targetEntityId = targetEntityId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public int getAbilityIndex() {
        return abilityIndex;
    }

    public void setAbilityIndex(int abilityIndex) {
        this.abilityIndex = abilityIndex;
    }

    public long getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(long targetEntityId) {
        this.targetEntityId = targetEntityId;
    }
}
