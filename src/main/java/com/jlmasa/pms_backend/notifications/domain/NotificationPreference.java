package com.jlmasa.pms_backend.notifications.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_membership_id", nullable = false, updatable = false)
    private UUID organizationMembershipId;

    @Column(name = "event_type", nullable = false, updatable = false, length = 50)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_channel", nullable = false, updatable = false, length =10)
    private DeliveryChannel deliveryChannel;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected NotificationPreference() {}

    public NotificationPreference(UUID organizationMembershipId, String eventType, DeliveryChannel channel, boolean enabled, Instant now){
        this.organizationMembershipId = Objects.requireNonNull(organizationMembershipId, "organizationMembershipId");
        this.eventType = Objects.requireNonNull(eventType, "eventType");
        this.deliveryChannel = Objects.requireNonNull(channel, "channel");
        this.enabled = enabled;
        this.updatedAt = now;
    }

    public void toggle(boolean enabled, Instant now) {this.enabled = enabled; this.updatedAt = now;}
    public UUID getId() {return id;}
    public UUID getOrganizationMembershipId() {return organizationMembershipId;}
    public String getEventType() {return eventType;}
    public DeliveryChannel getDeliveryChannel() {return deliveryChannel;}
    public boolean isEnabled() {return enabled;}
    public Instant getUpdatedAt(){return updatedAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof NotificationPreference other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
