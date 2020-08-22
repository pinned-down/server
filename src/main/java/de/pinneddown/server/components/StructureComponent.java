package de.pinneddown.server.components;

public class StructureComponent {
    private int baseStructure;
    private int structureModifier;

    public int getBaseStructure() {
        return baseStructure;
    }

    public void setBaseStructure(int baseStructure) {
        this.baseStructure = baseStructure;
    }

    public int getStructureModifier() {
        return structureModifier;
    }

    public void setStructureModifier(int structureModifier) {
        this.structureModifier = structureModifier;
    }

    public int getCurrentStructure() {
        return baseStructure + structureModifier;
    }
}
