package de.pinneddown.server.components;

public class PowerComponent {
    private int basePower;
    private int powerModifier;

    public int getBasePower() {
        return basePower;
    }

    public void setBasePower(int basePower) {
        this.basePower = basePower;
    }

    public int getPowerModifier() {
        return powerModifier;
    }

    public void setPowerModifier(int powerModifier) {
        this.powerModifier = powerModifier;
    }

    public int getCurrentPower() {
        return basePower + powerModifier;
    }
}
