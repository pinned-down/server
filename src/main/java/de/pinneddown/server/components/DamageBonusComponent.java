package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class DamageBonusComponent implements EntityComponent {
    private int damageBonus;

    public int getDamageBonus() {
        return damageBonus;
    }

    public void setDamageBonus(int damageBonus) {
        this.damageBonus = damageBonus;
    }
}
