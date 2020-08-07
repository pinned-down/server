package de.pinneddown.server;

public class WebSocketMessage {
    private String messageType;
    private Object messageData;

    public WebSocketMessage() {
    }

    public WebSocketMessage(Object message) {
        this.messageType = message.getClass().getTypeName();
        this.messageData = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Object getMessageData() {
        return messageData;
    }

    public void setMessageData(Object messageData) {
        this.messageData = messageData;
    }
}
