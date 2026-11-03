package com.jlmasa.pms_backend.files.entity;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "message_attachments")
public class MessageAttachment {

    @EmbeddedId
    private MessageAttachmentId id;

    @Column(name = "message_id", insertable = false, updatable = false)
    private UUID messageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("fileVersionId")
    @JoinColumn(name = "file_version_id", nullable = false, updatable = false)
    private FileVersion fileVersion;

    @Column(name = "attached_by_membership_id", nullable = false, updatable = false)
    private UUID attachedByMembershipId;

    @Column(name = "attached_at", nullable = false, updatable = false)
    private Instant attachedAt;

    protected MessageAttachment() {}

    public MessageAttachment(UUID messageId, FileVersion fileVersion, UUID attachedByMembershipId, Instant now){
        this.messageId = Objects.requireNonNull(messageId, "messageId");
        this.fileVersion = Objects.requireNonNull(fileVersion, "fileVersion");
        this.id = new MessageAttachmentId(messageId, fileVersion.getId());
        this.attachedByMembershipId = Objects.requireNonNull(attachedByMembershipId, "attachedByMembershipId");
        this.attachedAt = now;
    }

    public MessageAttachmentId getId() {return id;}
    public UUID getMessageId() {return messageId;}
    public FileVersion  getFileVersion() {return fileVersion;}
    public UUID getAttachedByMembershipId() {return attachedByMembershipId;}
    public Instant getAttachedAt() {return attachedAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof MessageAttachment other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
