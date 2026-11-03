package com.jlmasa.pms_backend.messaging.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "message_reactions")
public class MessageReaction {

    @EmbeddedId
    private MessageReactionId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("messageId")
    @JoinColumn(name = "message_id", nullable = false, updatable = false)
    private Message message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected MessageReaction(){}

    public MessageReaction(Message message, UUID reactedByMembershipId, String emoji, Instant now){
        this.message = Objects.requireNonNull(message, "message");
        this.id = new MessageReactionId(message.getId(), reactedByMembershipId, emoji);
        this.createdAt = now;
    }
    public MessageReactionId get() {return id;}
    public Message getMessage() {return message;}
    public Instant getCreatedAt() {return createdAt;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof MessageReaction other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
