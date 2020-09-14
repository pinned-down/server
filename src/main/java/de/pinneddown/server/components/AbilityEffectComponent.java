package de.pinneddown.server.components;

import de.pinneddown.server.AbilityEffectDuration;
import de.pinneddown.server.EntityComponent;

public class AbilityEffectComponent implements EntityComponent {
    private String abilityEffectDuration;
    private long targetEntityId;

    public String getAbilityEffectDuration() {
        return abilityEffectDuration;
    }

    public void setAbilityEffectDuration(String abilityEffectDuration) {
        this.abilityEffectDuration = abilityEffectDuration;
    }

    public AbilityEffectDuration getDuration() {
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
}
