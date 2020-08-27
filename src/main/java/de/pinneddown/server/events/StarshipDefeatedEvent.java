package de.pinneddown.server.events;

public class StarshipDefeatedEvent {
    private long entityId;
    private boolean overpowered;

    public StarshipDefeatedEvent() {
    }

    public StarshipDefeatedEvent(long entityId) {
        this.entityId = entityId;
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
}
