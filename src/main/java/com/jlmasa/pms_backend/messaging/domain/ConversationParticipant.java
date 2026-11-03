package com.jlmasa.pms_backend.messaging.domain;


import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conversation_participants")
public class ConversationParticipant {

    @EmbeddedId
    private ConversationParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("conversationId")
    @JoinColumn(name = "conversation_id", nullable = false, updatable = false)
    private Conversation conversation;

    @Column(name = "organization_membership_id", insertable = false, updatable = false)
    private UUID organizationMembershipId;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_role", nullable = false, length = 10)
    private ParticipantRole participantRole;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Column(name = "removed_at")
    private Instant removedAt;

    @Column(name = "last_read_at")
    private Instant lastReadAt;

    @Column(name = "notification_muted", nullable = false)
    private boolean notificationsMuted;

    protected ConversationParticipant() {}

    public ConversationParticipant(Conversation conversation, UUID organizationMembershipId, ParticipantRole role, Instant now){
        this.conversation = Objects.requireNonNull(conversation, "conversation" );
        this.organizationMembershipId = Objects.requireNonNull(organizationMembershipId, "organizationMembershipId");
        this.id = new ConversationParticipantId(conversation.getId(), organizationMembershipId);
        this.participantRole = Objects.requireNonNull(role, "role"  );
        this.joinedAt = now;
        this.notificationsMuted = false;
    }

    public boolean isActive(){return removedAt == null;}
    public void remove(Instant now) {this.removedAt = now;}
    public void markReadUpTo(Instant now) {this.lastReadAt = now;}
    public void mute() {this.notificationsMuted = true;}
    public void unmute() {this.notificationsMuted = false;}

    public ConversationParticipantId getId() {return id;}
    public Conversation getConversation() {return conversation;}
    public UUID getOrganizationMembershipId() {return organizationMembershipId;}
    public ParticipantRole getParticipantRole() {return participantRole;}
    public Instant getJoinedAt() {return joinedAt;}
    public Instant getRemovedAt() {return removedAt;}
    public Instant getLastReadAt() {return lastReadAt;}
    public boolean isNotificationsMuted() {return notificationsMuted;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof ConversationParticipant other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
