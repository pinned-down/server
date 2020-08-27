package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

public class AttachmentComponent implements EntityComponent {
    private long entityId;
    private long attachedTo;

    public AttachmentComponent() {
    }

    public AttachmentComponent(long entityId, long attachedTo) {
        this.entityId = entityId;
        this.attachedTo = attachedTo;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public long getAttachedTo() {
        return attachedTo;
    }

    public void setAttachedTo(long attachedTo) {
        this.attachedTo = attachedTo;
    }
}
