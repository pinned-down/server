package de.pinneddown.server.events;

public class StarshipPowerChangedEvent {
    private long entityId;
    private int oldPowerModifier;
    private int newPowerModifier;

    public StarshipPowerChangedEvent() {
    }

    public StarshipPowerChangedEvent(long entityId, int oldPowerModifier, int newPowerModifier) {
        this.entityId = entityId;
        this.oldPowerModifier = oldPowerModifier;
        this.newPowerModifier = newPowerModifier;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public int getOldPowerModifier() {
        return oldPowerModifier;
    }

    public void setOldPowerModifier(int oldPowerModifier) {
        this.oldPowerModifier = oldPowerModifier;
    }

    public int getNewPowerModifier() {
        return newPowerModifier;
    }

    public void setNewPowerModifier(int newPowerModifier) {
        this.newPowerModifier = newPowerModifier;
    }
}
