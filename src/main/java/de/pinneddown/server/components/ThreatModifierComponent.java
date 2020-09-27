package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

import java.util.HashMap;

public class ThreatModifierComponent implements EntityComponent {
    private HashMap<String, Integer> threatModifiers;

    public HashMap<String, Integer> getThreatModifiers() {
        return threatModifiers;
    }

    public void setThreatModifiers(HashMap<String, Integer> threatModifiers) {
        this.threatModifiers = threatModifiers;
    }
}
