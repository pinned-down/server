package de.pinneddown.server.components;

import de.pinneddown.server.BlueprintManager;
import de.pinneddown.server.EntityComponent;

import java.util.ArrayList;

public class AbilitiesComponent implements EntityComponent {
    private ArrayList<String> abilities;
    private ArrayList<Long> abilityEntities;

    public ArrayList<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(ArrayList<String> abilities) {
        this.abilities = abilities;
    }

    public ArrayList<Long> getAbilityEntities() {
        return abilityEntities;
    }

    public ArrayList<Long> getOrCreateAbilityEntities(BlueprintManager blueprintManager) {
        if (abilityEntities == null) {
            abilityEntities = new ArrayList<>();

            for (String abilityBlueprintId : abilities) {
                long abilityEntityId = blueprintManager.createEntity(abilityBlueprintId);
                abilityEntities.add(abilityEntityId);
            }
        }

        return abilityEntities;
    }

    public void setAbilityEntities(ArrayList<Long> abilityEntities) {
        this.abilityEntities = abilityEntities;
    }
}
