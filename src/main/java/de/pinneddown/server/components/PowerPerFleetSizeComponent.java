package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class PowerPerFleetSizeComponent implements EntityComponent {
    private int powerPerFleetSize;
    private int appliedPowerPerFleetSize;
    private String fleetGameplayTagFilter;

    public int getPowerPerFleetSize() {
        return powerPerFleetSize;
    }

    public void setPowerPerFleetSize(int powerPerFleetSize) {
        this.powerPerFleetSize = powerPerFleetSize;
    }

    public int getAppliedPowerPerFleetSize() {
        return appliedPowerPerFleetSize;
    }

    public void setAppliedPowerPerFleetSize(int appliedPowerPerFleetSize) {
        this.appliedPowerPerFleetSize = appliedPowerPerFleetSize;
    }

    public String getFleetGameplayTagFilter() {
        return fleetGameplayTagFilter;
    }

    public void setFleetGameplayTagFilter(String fleetGameplayTagFilter) {
        this.fleetGameplayTagFilter = fleetGameplayTagFilter;
    }
}
