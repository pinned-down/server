package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class PowerPerLocationComponent implements EntityComponent {
    private int powerPerLocation;
    private int appliedPowerPerLocation;

    public int getPowerPerLocation() {
        return powerPerLocation;
    }

    public void setPowerPerLocation(int powerPerLocation) {
        this.powerPerLocation = powerPerLocation;
    }

    public int getAppliedPowerPerLocation() {
        return appliedPowerPerLocation;
    }

    public void setAppliedPowerPerLocation(int appliedPowerPerLocation) {
        this.appliedPowerPerLocation = appliedPowerPerLocation;
    }
}
