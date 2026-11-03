package com.jlmasa.pms_backend.messaging.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "message_mentions")
public class MessageMention {

    @EmbeddedId
    private MessageMentionId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("messageId")
    @JoinColumn(name = "message_id", nullable = false, updatable = false)
    private Message message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected MessageMention() {}

    public MessageMention(Message message, UUID mentionedMembershipId, Instant now){
        this.message = Objects.requireNonNull(message, "message");
        this.id = new MessageMentionId(message.getId(), mentionedMembershipId);
        this.createdAt = now;
    }

    public MessageMentionId getId() {return id;}
    public Message getMessage() {return message;}
    public Instant getCreatedAt() {return createdAt;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof MessageMention other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}

}
