package de.pinneddown.server.events;

import de.pinneddown.server.TurnPhase;

public class TurnPhaseStartedEvent {
    private TurnPhase turnPhase;

    public TurnPhaseStartedEvent() {
    }

    public TurnPhaseStartedEvent(TurnPhase turnPhase) {
        this.turnPhase = turnPhase;
    }

    public TurnPhase getTurnPhase() {
        return turnPhase;
    }

    public void setTurnPhase(TurnPhase turnPhase) {
        this.turnPhase = turnPhase;
    }

    @Override
    public String toString() {
        return "TurnPhaseStartedEvent{" +
                "turnPhase=" + turnPhase +
                '}';
    }
}
