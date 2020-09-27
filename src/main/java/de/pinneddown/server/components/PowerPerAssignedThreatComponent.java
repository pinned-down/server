package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class PowerPerAssignedThreatComponent implements EntityComponent {
    private int powerPerThreat;
    private int appliedPowerPerThreat;

    public int getPowerPerThreat() {
        return powerPerThreat;
    }

    public void setPowerPerThreat(int powerPerThreat) {
        this.powerPerThreat = powerPerThreat;
    }

    public int getAppliedPowerPerThreat() {
        return appliedPowerPerThreat;
    }

    public void setAppliedPowerPerThreat(int appliedPowerPerThreat) {
        this.appliedPowerPerThreat = appliedPowerPerThreat;
    }
}
