package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class ThreatComponent implements EntityComponent {
    private int threat;

    public int getThreat() {
        return threat;
    }

    public void setThreat(int threat) {
        this.threat = threat;
    }
}
