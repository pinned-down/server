package de.pinneddown.server;

import java.util.HashMap;

public class ComponentManager {
    private HashMap<Long, EntityComponent> components;

    public ComponentManager() {
        this.components = new HashMap<>();
    }

    public void addComponent(long entityId, EntityComponent component) {
        this.components.put(entityId, component);
    }

    public EntityComponent getComponent(long entityId) {
        return components.getOrDefault(entityId, null);
    }

    public EntityComponent removeComponent(long entityId) {
        return components.remove(entityId);
    }
}
