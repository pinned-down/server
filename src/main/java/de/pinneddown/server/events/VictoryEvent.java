package de.pinneddown.server.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class VictoryEvent {
    @Override
    public String toString() {
        return "VictoryEvent{}";
    }
}
