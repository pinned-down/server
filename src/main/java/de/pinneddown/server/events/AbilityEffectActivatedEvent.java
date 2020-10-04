package de.pinneddown.server.events;

public class AbilityEffectActivatedEvent {
    private long effectEntityId;
    private long targetEntityId;

    public AbilityEffectActivatedEvent() {
    }

    public AbilityEffectActivatedEvent(long effectEntityId, long targetEntityId) {
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
}
