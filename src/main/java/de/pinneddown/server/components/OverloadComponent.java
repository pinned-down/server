package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class OverloadComponent implements EntityComponent {
    private int overloads;

    public int getOverloads() {
        return overloads;
    }

    public void setOverloads(int overloads) {
        this.overloads = overloads;
    }
}
