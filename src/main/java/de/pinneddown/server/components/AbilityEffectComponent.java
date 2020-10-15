package de.pinneddown.server.components;

import de.pinneddown.server.AbilityEffectDuration;
import de.pinneddown.server.EntityComponent;

public class AbilityEffectComponent implements EntityComponent {
    private String abilityEffectDuration;
    private long targetEntityId;
    private boolean active;

    public String getAbilityEffectDuration() {
        return abilityEffectDuration;
    }

    public void setAbilityEffectDuration(String abilityEffectDuration) {
        this.abilityEffectDuration = abilityEffectDuration;
    }

    public AbilityEffectDuration getAbilityEffectDurationEnum() {
        if (abilityEffectDuration.equals("EndOfFight")) {
            return AbilityEffectDuration.END_OF_FIGHT;
        } else {
            return AbilityEffectDuration.INDEFINITE;
        }
    }

    public long getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(long targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
