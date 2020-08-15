package de.pinneddown.server;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class EventManager {
    private ArrayList<GameEvent> newEvents;
    private ArrayList<GameEvent> currentEvents;
    private HashMap<Object, ArrayList<EventHandler>> handlers;

    public EventManager() {
        this.newEvents = new ArrayList<>();
        this.currentEvents = new ArrayList<>();
        this.handlers = new HashMap<>();
    }

    public void processEvents() {
        while (this.newEvents.size() > 0)
        {
            this.currentEvents.addAll(this.newEvents);
            this.newEvents.clear();

            for (GameEvent e : this.currentEvents)
            {
                processEvent(e);
            }

            this.currentEvents.clear();
        }
    }

    public void queueEvent(Object eventType, Object eventData)
    {
        queueEvent(new GameEvent(eventType, eventData));
    }

    public void queueEvent(GameEvent e)
    {
        this.newEvents.add(e);

        if (this.newEvents.size() == 1 && this.currentEvents.size() == 0) {
            processEvents();
        }
    }

    public void addEventHandler(Object eventType, EventHandler handler) {
        ArrayList<EventHandler> eventHandlers;

        if (!handlers.containsKey(eventType)) {
            eventHandlers = new ArrayList<>();
            handlers.put(eventType, eventHandlers);
        } else {
            eventHandlers = handlers.get(eventType);
        }

        eventHandlers.add(handler);
    }

    public void removeEventHandler(Object eventType, EventHandler handler) {
        if (!handlers.containsKey(eventType)) {
            return;
        }

        ArrayList<EventHandler> eventHandlers = handlers.get(eventType);
        eventHandlers.remove(handler);
    }

    private void processEvent(GameEvent event) {
        if (!handlers.containsKey(event.getEventType())) {
            return;
        }

        ArrayList<EventHandler> eventHandlers = handlers.get(event.getEventType());

        for (EventHandler handler : eventHandlers) {
            handler.handleEvent(event);
        }
    }
}
