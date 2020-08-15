package de.pinneddown.server;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class EntityManager {
    private HashMap<Class<? extends EntityComponent>, ComponentManager> componentManagers;
    private long nextEntityId;

    public EntityManager() {
        componentManagers = new HashMap<>();
    }

    public void addComponent(long entityId, EntityComponent component) {
        Class<? extends EntityComponent> componentClass = component.getClass();

        ComponentManager componentManager = getOrCreateComponentManager(componentClass);
        componentManager.addComponent(entityId, component);
    }

    public long createEntity() {
        return this.nextEntityId++;
    }

    public <T extends EntityComponent> T getComponent(long entityId, Class<T> componentClass) {
        ComponentManager componentManager = componentManagers.getOrDefault(componentClass, null);
        return componentManager != null ? (T)componentManager.getComponent(entityId) : null;
    }

    public <T extends EntityComponent> T removeComponent(long entityId, Class<T> componentClass) {
        ComponentManager componentManager = getOrCreateComponentManager(componentClass);
        return (T)componentManager.removeComponent(entityId);
    }

    public void removeEntity(long entityId) {
        // Remove components.
        for (ComponentManager manager : componentManagers.values())
        {
            manager.removeComponent(entityId);
        }
    }

    private ComponentManager getOrCreateComponentManager(Class<? extends EntityComponent> componentClass) {
        ComponentManager componentManager = componentManagers.get(componentClass);

        if (componentManager == null)
        {
            componentManager = new ComponentManager();
            componentManagers.put(componentClass, componentManager);
        }

        return componentManager;
    }
}
