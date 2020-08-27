package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class BlueprintComponent implements EntityComponent {
    private String blueprintId;

    public BlueprintComponent() {
    }

    public BlueprintComponent(String blueprintId) {
        this.blueprintId = blueprintId;
    }

    public String getBlueprintId() {
        return blueprintId;
    }

    public void setBlueprintId(String blueprintId) {
        this.blueprintId = blueprintId;
    }
}
