package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class InstigatorComponent implements EntityComponent {
    private long entityId;

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }
}
