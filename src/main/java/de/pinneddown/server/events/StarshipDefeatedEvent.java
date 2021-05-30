package de.pinneddown.server.events;

public class StarshipDefeatedEvent {
    private long entityId;
    private boolean overpowered;
    private long defeatedBy;

    public StarshipDefeatedEvent() {
    }

    public StarshipDefeatedEvent(long entityId, long defeatedBy) {
        this.entityId = entityId;
        this.defeatedBy = defeatedBy;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public boolean isOverpowered() {
        return overpowered;
    }

    public void setOverpowered(boolean overpowered) {
        this.overpowered = overpowered;
    }

    public long getDefeatedBy() {
        return defeatedBy;
    }

    public void setDefeatedBy(long defeatedBy) {
        this.defeatedBy = defeatedBy;
    }

    @Override
    public String toString() {
        return "StarshipDefeatedEvent{" +
                "entityId=" + entityId +
                ", overpowered=" + overpowered +
                ", defeatedBy=" + defeatedBy +
                '}';
    }
}
