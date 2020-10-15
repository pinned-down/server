package de.pinneddown.server.events;

import java.util.HashMap;

public class ThreatModifiersChangedEvent {
    private HashMap<String, Integer> threatModifiers;

    public ThreatModifiersChangedEvent() {
    }

    public ThreatModifiersChangedEvent(HashMap<String, Integer> threatModifiers) {
        this.threatModifiers = new HashMap<>(threatModifiers);
    }

    public HashMap<String, Integer> getThreatModifiers() {
        return threatModifiers;
    }

    public void setThreatModifiers(HashMap<String, Integer> threatModifiers) {
        this.threatModifiers = new HashMap<>(threatModifiers);
    }
}
