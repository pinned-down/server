package de.pinneddown.server;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class EntityManager {
    private EventManager eventManager;
    private HashMap<Class<? extends EntityComponent>, ComponentManager> componentManagers;
    private long nextEntityId;
    private ArrayList<Long> removedEntities;

    public EntityManager(EventManager eventManager) {
        this.eventManager = eventManager;
        this.componentManagers = new HashMap<>();
        this.nextEntityId = 1L;
        this.removedEntities = new ArrayList<>();

        this.eventManager.addAllEventsHandledHandler(this::onAllEventsHandled);
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
        removedEntities.add(entityId);
    }

    public void cleanUpEntities() {
        for (Long entityId : removedEntities) {
            // Remove components.
            for (ComponentManager manager : componentManagers.values())
            {
                manager.removeComponent(entityId);
            }
        }

        removedEntities.clear();
    }

    public void clear() {
        componentManagers.clear();

        nextEntityId = 0L;
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


    private void onAllEventsHandled() {
        cleanUpEntities();
    }
}
