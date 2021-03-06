package de.pinneddown.server.events;

public class AbilityEffectDeactivatedEvent {
    private long effectEntityId;
    private long targetEntityId;

    public AbilityEffectDeactivatedEvent() {
    }

    public AbilityEffectDeactivatedEvent(long effectEntityId, long targetEntityId) {
        this.effectEntityId = effectEntityId;
        this.targetEntityId = targetEntityId;
    }

    public long getEffectEntityId() {
        return effectEntityId;
    }

    public void setEffectEntityId(long effectEntityId) {
        this.effectEntityId = effectEntityId;
    }

    public long getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(long targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    @Override
    public String toString() {
        return "AbilityEffectDeactivatedEvent{" +
                "effectEntityId=" + effectEntityId +
                ", targetEntityId=" + targetEntityId +
                '}';
    }
}
