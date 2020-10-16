package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class ThreatPoolConditionComponent implements EntityComponent {
    private int minimumThreat;
    private int maximumThreat;

    public int getMinimumThreat() {
        return minimumThreat;
    }

    public void setMinimumThreat(int minimumThreat) {
        this.minimumThreat = minimumThreat;
    }

    public int getMaximumThreat() {
        return maximumThreat;
    }

    public void setMaximumThreat(int maximumThreat) {
        this.maximumThreat = maximumThreat;
    }
}
