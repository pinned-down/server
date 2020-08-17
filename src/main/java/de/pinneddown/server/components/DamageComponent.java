package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class DamageComponent implements EntityComponent {
    private int structureDamage;

    public int getStructureDamage() {
        return structureDamage;
    }

    public void setStructureDamage(int structureDamage) {
        this.structureDamage = structureDamage;
    }
}
