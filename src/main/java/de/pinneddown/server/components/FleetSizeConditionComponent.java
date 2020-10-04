package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class FleetSizeConditionComponent implements EntityComponent {
    private int minFleetSize;
    private int maxFleetSize;

    public int getMinFleetSize() {
        return minFleetSize;
    }

    public void setMinFleetSize(int minFleetSize) {
        this.minFleetSize = minFleetSize;
    }

    public int getMaxFleetSize() {
        return maxFleetSize;
    }

    public void setMaxFleetSize(int maxFleetSize) {
        this.maxFleetSize = maxFleetSize;
    }
}
