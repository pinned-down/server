package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

import java.util.ArrayList;

public class AbilitiesComponent implements EntityComponent {
    private ArrayList<String> abilities;

    public ArrayList<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(ArrayList<String> abilities) {
        this.abilities = abilities;
    }
}
