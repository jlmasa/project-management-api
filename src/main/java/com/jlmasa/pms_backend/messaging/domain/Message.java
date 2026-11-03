package com.jlmasa.pms_backend.messaging.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class Message {

    @Id @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false) private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;


    @Column(name = "sender_membership_id", nullable = false)
    private UUID senderMembershipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 10)
    private MessageType messageType;

    @Column(name = "body")
    private String body;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "edited_at")
    private Instant editedAt;

    @Column(name = "deleted_at" )
    private Instant deletedAt;

    protected Message() {}

    public Message(Conversation conversation, UUID senderMembershipId, String body, Instant now){
        this.conversation = Objects.requireNonNull(conversation, "conversation");
        this.senderMembershipId  = Objects.requireNonNull(senderMembershipId, "senderMembershipId");
        this.messageType = MessageType.TEXT;
        this.body = body;
        this.createdAt = now;
    }
    public void edit(String newBody, Instant now) {this.body = newBody; this.editedAt = now;}
    public void softDelete(Instant now){this.deletedAt = now;}
    public boolean isDeleted(){return deletedAt != null;}

    public UUID getId() {return id;}
    public Conversation getConversation() {return conversation;}
    public UUID getSenderMembershipId() {return senderMembershipId;}
    public Message getParentMessage() {return parentMessage;}
    public MessageType getMessageType() {return messageType;}
    public String getBody() {return body;}
    public Instant getCreatedAt(){return createdAt;}
    public Instant getEditedAt() {return editedAt;}
    public Instant getDeletedAt() {return deletedAt;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Message other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
