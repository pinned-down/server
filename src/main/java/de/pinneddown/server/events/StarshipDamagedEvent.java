package de.pinneddown.server.events;

public class StarshipDamagedEvent {
    private long starshipEntityId;
    private long damageEntityId;
    private String damageBlueprintId;

    public StarshipDamagedEvent() {
    }

    public StarshipDamagedEvent(long starshipEntityId, long damageEntityId, String damageBlueprintId) {
        this.starshipEntityId = starshipEntityId;
        this.damageEntityId = damageEntityId;
        this.damageBlueprintId = damageBlueprintId;
    }

    public long getStarshipEntityId() {
        return starshipEntityId;
    }

    public void setStarshipEntityId(long starshipEntityId) {
        this.starshipEntityId = starshipEntityId;
    }

    public long getDamageEntityId() {
        return damageEntityId;
    }

    public void setDamageEntityId(long damageEntityId) {
        this.damageEntityId = damageEntityId;
    }

    public String getDamageBlueprintId() {
        return damageBlueprintId;
    }

    public void setDamageBlueprintId(String damageBlueprintId) {
        this.damageBlueprintId = damageBlueprintId;
    }

    @Override
    public String toString() {
        return "StarshipDamagedEvent{" +
                "starshipEntityId=" + starshipEntityId +
                ", damageEntityId=" + damageEntityId +
                ", damageBlueprintId='" + damageBlueprintId + '\'' +
                '}';
    }
}
