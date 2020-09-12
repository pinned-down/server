package de.pinneddown.server.components;

import de.pinneddown.server.AbilityEffectDuration;
import de.pinneddown.server.EntityComponent;

public class AbilityEffectComponent implements EntityComponent {
    private String abilityEffectDuration;

    public String getAbilityEffectDuration() {
        return abilityEffectDuration;
    }

    public void setAbilityEffectDuration(String abilityEffectDuration) {
        this.abilityEffectDuration = abilityEffectDuration;
    }

    public AbilityEffectDuration getDuration() {
        if (abilityEffectDuration == "EndOfFight") {
            return AbilityEffectDuration.END_OF_FIGHT;
        } else {
            return AbilityEffectDuration.INDEFINITE;
        }
    }
}
