package com.jlmasa.pms_backend.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(name = "recipient_membership_id", nullable = false,updatable = false)
    private UUID recipientMembershipId;

    @Column(name = "actor_membership_id", updatable = false)
    private UUID actorMembershipId;

    @Column(name = "notification_type", nullable = false, updatable = false, length = 50)
    private String notificationType;

    @Column(name = "title", nullable = false, updatable = false)
    private String title;

    @Column(name = "body", updatable = false)
    private String body;

    @Column(name = "target_project_id", updatable = false)
    private UUID targetProjectId;

    @Column(name = "target_task_id", updatable = false)
    private UUID targetTaskId;

    @Column(name = "target_conversation_id", updatable = false)
    private UUID targetConversationId;

    @Column(name = "target_message_id", updatable = false)
    private UUID targetMessageId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "dismissed_at")
    private Instant dismissedAt;

    protected Notification() {}

    public Notification(UUID organizationId, UUID recipientMembershipId, UUID actorMembershipId, String notificationType, String title, Instant now){
        this.organizationId = Objects.requireNonNull(organizationId,"organizationId");
        this.recipientMembershipId = Objects.requireNonNull(recipientMembershipId, "recipientMembershipId");
        this.actorMembershipId = actorMembershipId;
        this.notificationType = Objects.requireNonNull(notificationType, "notificationType");
        this.title = Objects.requireNonNull(title, "title");
        this.createdAt = now;
    }

    public void markRead(Instant now) {this.readAt = now;}
    public void dismiss(Instant now) {this.dismissedAt = now;}
    public boolean isUnread() {return readAt == null;}

    public UUID getId() {return id;}
    public UUID getOrganizationId() {return organizationId;}
    public UUID getRecipientMembershipId() {return recipientMembershipId;}
    public UUID getActorMembershipId() {return actorMembershipId;}
    public String getNotificationType() {return notificationType;}
    public String getTitle() {return title;}
    public String getBody() {return body;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getReadAt() {return readAt;}
    public Instant getDismissedAt() {return dismissedAt;}

    @Override
    public boolean equals(Object o){
        if (this ==o) return true;
        if(!(o instanceof Notification other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
