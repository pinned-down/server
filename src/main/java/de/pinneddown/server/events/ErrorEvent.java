package de.pinneddown.server.events;

import de.pinneddown.server.ErrorCode;

public class ErrorEvent {
    private String actionId;
    private ErrorCode errorCode;

    public ErrorEvent() {
    }

    public ErrorEvent(String actionId, ErrorCode errorCode) {
        this.actionId = actionId;
        this.errorCode = errorCode;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
