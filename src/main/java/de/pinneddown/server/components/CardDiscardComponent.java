package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class CardDiscardComponent implements EntityComponent {
    private int discardedRandomCards;

    public int getDiscardedRandomCards() {
        return discardedRandomCards;
    }

    public void setDiscardedRandomCards(int discardedRandomCards) {
        this.discardedRandomCards = discardedRandomCards;
    }
}
