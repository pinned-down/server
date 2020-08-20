package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class DistanceComponent implements EntityComponent {
    private int distance;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
