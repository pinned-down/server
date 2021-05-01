package de.pinneddown.server.actions;

public class JoinGameAction extends PlayerAction {
    private String ticket;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
