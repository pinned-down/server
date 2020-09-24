package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class PowerDifferenceConditionComponent implements EntityComponent {
    private int requiredPowerDifference;

    public int getRequiredPowerDifference() {
        return requiredPowerDifference;
    }

    public void setRequiredPowerDifference(int requiredPowerDifference) {
        this.requiredPowerDifference = requiredPowerDifference;
    }
}
