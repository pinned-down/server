package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class ThreatChangeComponent implements EntityComponent {
    private int threatChange;

    public int getThreatChange() {
        return threatChange;
    }

    public void setThreatChange(int threatChange) {
        this.threatChange = threatChange;
    }
}
