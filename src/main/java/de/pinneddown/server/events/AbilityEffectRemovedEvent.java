package de.pinneddown.server.events;

public class AbilityEffectRemovedEvent {
    private long effectEntityId;
    private long targetEntityId;

    public AbilityEffectRemovedEvent() {
    }

    public AbilityEffectRemovedEvent(long effectEntityId, long targetEntityId) {
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
        return "AbilityEffectRemovedEvent{" +
                "effectEntityId=" + effectEntityId +
                ", targetEntityId=" + targetEntityId +
                '}';
    }
}
