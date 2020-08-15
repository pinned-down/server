package de.pinneddown.server;

@FunctionalInterface
public interface EventHandler {
    void handleEvent(GameEvent event);
}
