package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class PowerBonusComponent implements EntityComponent {
    private int powerBonus;
    private int appliedPowerBonus;

    public int getPowerBonus() {
        return powerBonus;
    }

    public void setPowerBonus(int powerBonus) {
        this.powerBonus = powerBonus;
    }

    public int getAppliedPowerBonus() {
        return appliedPowerBonus;
    }

    public void setAppliedPowerBonus(int appliedPowerBonus) {
        this.appliedPowerBonus = appliedPowerBonus;
    }
}
