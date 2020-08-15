package de.pinneddown.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import java.util.ArrayList;

public class BlueprintSet {
    private String hash;

    @JsonProperty("records")
    private ArrayList<Blueprint> blueprints;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Blueprint getBlueprint(String blueprintId) {
        if (Strings.isNullOrEmpty(blueprintId)) {
            return null;
        }

        for (Blueprint blueprint : blueprints) {
            if (blueprintId.equals(blueprint.getId())) {
                return blueprint;
            }
        }

        return null;
    }
    public ArrayList<Blueprint> getBlueprints() {
        return blueprints;
    }

    public void setBlueprints(ArrayList<Blueprint> blueprints) {
        this.blueprints = blueprints;
    }
}
