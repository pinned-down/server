package de.pinneddown.server;

public class GameEvent {
    private Object eventType;
    private Object eventData;

    public GameEvent() {
    }

    public GameEvent(Object eventType, Object eventData) {
        this.eventType = eventType;
        this.eventData = eventData;
    }

    public Object getEventType() {
        return eventType;
    }

    public void setEventType(Object eventType) {
        this.eventType = eventType;
    }

    public Object getEventData() {
        return eventData;
    }

    public void setEventData(Object eventData) {
        this.eventData = eventData;
    }
}
