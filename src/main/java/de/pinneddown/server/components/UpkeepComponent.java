package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class UpkeepComponent implements EntityComponent {
    private int upkeep;

    public int getUpkeep() {
        return upkeep;
    }

    public void setUpkeep(int upkeep) {
        this.upkeep = upkeep;
    }
}
