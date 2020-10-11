package de.pinneddown.server.events;

public class BattleDestinyDrawnEvent {
    private long targetEntityId;
    private String battleDestinyCardBlueprintId;
    private int battleDestiny;

    public BattleDestinyDrawnEvent() {
    }

    public BattleDestinyDrawnEvent(long targetEntityId, String battleDestinyCardBlueprintId, int battleDestiny) {
        this.targetEntityId = targetEntityId;
        this.battleDestinyCardBlueprintId = battleDestinyCardBlueprintId;
        this.battleDestiny = battleDestiny;
    }

    public long getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(long targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    public String getBattleDestinyCardBlueprintId() {
        return battleDestinyCardBlueprintId;
    }

    public void setBattleDestinyCardBlueprintId(String battleDestinyCardBlueprintId) {
        this.battleDestinyCardBlueprintId = battleDestinyCardBlueprintId;
    }

    public int getBattleDestiny() {
        return battleDestiny;
    }

    public void setBattleDestiny(int battleDestiny) {
        this.battleDestiny = battleDestiny;
    }
}
