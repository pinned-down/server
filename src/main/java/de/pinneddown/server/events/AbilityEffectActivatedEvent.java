package de.pinneddown.server.events;

public class AbilityEffectActivatedEvent {
    private long effectEntityId;
    private String effectBlueprintId;
    private String abilityBlueprintId;
    private long targetEntityId;

    public AbilityEffectActivatedEvent() {
    }

    public AbilityEffectActivatedEvent(long effectEntityId, String effectBlueprintId, String abilityBlueprintId, long targetEntityId) {
        this.effectEntityId = effectEntityId;
        this.effectBlueprintId = effectBlueprintId;
        this.abilityBlueprintId = abilityBlueprintId;
        this.targetEntityId = targetEntityId;
    }

    public long getEffectEntityId() {
        return effectEntityId;
    }

    public void setEffectEntityId(long effectEntityId) {
        this.effectEntityId = effectEntityId;
    }

    public String getEffectBlueprintId() {
        return effectBlueprintId;
    }

    public void setEffectBlueprintId(String effectBlueprintId) {
        this.effectBlueprintId = effectBlueprintId;
    }

    public String getAbilityBlueprintId() {
        return abilityBlueprintId;
    }

    public void setAbilityBlueprintId(String abilityBlueprintId) {
        this.abilityBlueprintId = abilityBlueprintId;
    }

    public long getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(long targetEntityId) {
        this.targetEntityId = targetEntityId;
    }
}
