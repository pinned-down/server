package de.pinneddown.server.events;

public class AbilityEffectAppliedEvent {
    private long effectEntityId;
    private long targetEntityId;

    public AbilityEffectAppliedEvent() {
    }

    public AbilityEffectAppliedEvent(long effectEntityId, long targetEntityId) {
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
        return "AbilityEffectAppliedEvent{" +
                "effectEntityId=" + effectEntityId +
                ", targetEntityId=" + targetEntityId +
                '}';
    }
}
