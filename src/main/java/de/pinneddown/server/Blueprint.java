package de.pinneddown.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Blueprint {
    private String id;
    private LinkedHashMap attributes;
    private ArrayList<String> components;

    @JsonProperty("parent_id")
    private String parentId;

    public Blueprint() {
        this.attributes = new LinkedHashMap();
        this.components = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LinkedHashMap getAttributes() {
        return attributes;
    }

    public void setAttributes(LinkedHashMap attributes) {
        this.attributes = attributes;
    }

    public ArrayList<String> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<String> components) {
        this.components = components;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
