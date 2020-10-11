package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class BattleDestinyComponent implements EntityComponent {
    private int battleDestinyCardsDrawn;
    private int appliedBattleDestinyPower;

    public int getBattleDestinyCardsDrawn() {
        return battleDestinyCardsDrawn;
    }

    public void setBattleDestinyCardsDrawn(int battleDestinyCardsDrawn) {
        this.battleDestinyCardsDrawn = battleDestinyCardsDrawn;
    }

    public int getAppliedBattleDestinyPower() {
        return appliedBattleDestinyPower;
    }

    public void setAppliedBattleDestinyPower(int appliedBattleDestinyPower) {
        this.appliedBattleDestinyPower = appliedBattleDestinyPower;
    }
}
