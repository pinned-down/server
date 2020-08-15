package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class OwnerComponent implements EntityComponent {
    private long owner;

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }
}
