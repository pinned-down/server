package de.pinneddown.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;

@Component
public class BlueprintManager implements ApplicationListener<ApplicationReadyEvent> {
    private EntityManager entityManager;
    private BlueprintSet blueprints;

    private Logger logger = LoggerFactory.getLogger(BlueprintManager.class);

    @Value("classpath:PinnedDown.json")
    Resource blueprintFile;

    public BlueprintManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        try {
            loadBlueprints();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBlueprints() throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        blueprints = jsonMapper.readValue(blueprintFile.getURL(), BlueprintSet.class);
    }

    public long createEntity(String blueprintId) {
        // Get blueprint.
        Blueprint blueprint = blueprints.getBlueprint(blueprintId);

        if (blueprint == null) {
            throw new IllegalArgumentException("Blueprint not found: " + blueprintId);
        }

        // Create entity.
        long entityId = entityManager.createEntity();

        // Add components.
        HashSet<String> components = buildComponentSet(blueprint);
        LinkedHashMap attributes = buildAttributes(blueprint);

        for (String componentName : components) {
            try {
                Class componentClass = Class.forName("de.pinneddown.server.components." + componentName);
                EntityComponent component = (EntityComponent)componentClass.newInstance();
                PropertyAccessor propertyAccessor = PropertyAccessorFactory.forBeanPropertyAccess(component);

                for (Object key : attributes.keySet()) {
                    if (propertyAccessor.isWritableProperty(key.toString())) {
                        propertyAccessor.setPropertyValue(key.toString(), attributes.get(key));
                    }
                }

                entityManager.addComponent(entityId, component);
            } catch (ClassNotFoundException e) {
                logger.warn("Unknown component class " + componentName + " for blueprint " + blueprintId + ".");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        return entityId;
    }

    private HashSet<String> buildComponentSet(Blueprint blueprint) {
        return buildComponentSetRecursively(blueprint, new HashSet<String>());
    }

    private HashSet<String> buildComponentSetRecursively(Blueprint blueprint, HashSet<String> currentComponents) {
        if (blueprint == null) {
            return currentComponents;
        }

        for (String componentName : blueprint.getComponents()) {
            currentComponents.add(componentName);
        }

        Blueprint parent = blueprints.getBlueprint(blueprint.getParentId());
        return buildComponentSetRecursively(parent, currentComponents);
    }

    private LinkedHashMap buildAttributes(Blueprint blueprint) {
        return buildAttributesRecursively(blueprint, new LinkedHashMap());
    }

    private LinkedHashMap buildAttributesRecursively(Blueprint blueprint, LinkedHashMap currentAttributes) {
        if (blueprint == null) {
            return currentAttributes;
        }

        for (Object key : blueprint.getAttributes().keySet()) {
            currentAttributes.putIfAbsent(key, blueprint.getAttributes().get(key));
        }

        Blueprint parent = blueprints.getBlueprint(blueprint.getParentId());
        return buildAttributesRecursively(parent, currentAttributes);
    }
}
