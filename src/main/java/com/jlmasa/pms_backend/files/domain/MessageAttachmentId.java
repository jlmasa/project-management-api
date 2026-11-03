package com.jlmasa.pms_backend.files.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MessageAttachmentId implements Serializable {

    private UUID messageId;
    private UUID fileVersionId;

    protected MessageAttachmentId() {}

    public MessageAttachmentId(UUID messageId, UUID fileVersionId){
        this.messageId = Objects.requireNonNull(messageId);
        this.fileVersionId = Objects.requireNonNull(fileVersionId);
    }
    public UUID getMessageId() {return messageId;}
    public UUID getFileVersionId() {return fileVersionId;}

    @Override
    public boolean equals(Object o){
        if (this == o)return true;
        if(!(o instanceof MessageAttachmentId other)) return false;
        return messageId.equals(other.messageId) && fileVersionId.equals(other.fileVersionId);
    }
    @Override
    public int hashCode() {return Objects.hash(messageId, fileVersionId);}
}
