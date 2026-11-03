package com.jlmasa.pms_backend.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(name = "actor_membership_id", updatable = false)
    private UUID actorMembershipId;

    @Column(name = "target_membership_id", updatable = false)
    private UUID targetMembershipId;

    @Column(name = "action", nullable = false, updatable = false, length = 100)
    private String action;

    @Column(name = "entity_type", nullable = false, updatable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", updatable = false)
    private UUID entityId;

    @Column(name = "ip_address", updatable = false, length = 64)
    private String ipAddress;

    @Column(name = "user_agent", updatable = false)
    private String userAgent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", updatable = false)
    private String details;

    @Column(name = "happened_at", nullable = false, updatable = false)
    private Instant happenedAt;

    protected AuditLog(){}

    public AuditLog(UUID organizationId, String action, String entityType, UUID entityId, Instant now){
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId");
        this.action = Objects.requireNonNull(action, "action");
        this.entityType = Objects.requireNonNull(entityType, "entityType");
        this.entityId = entityId;
        this.happenedAt = now;
    }

    public void withActor(UUID actorMembershipId) {this.actorMembershipId = actorMembershipId;}
    public void withTarget(UUID targetMembershipId) {this.targetMembershipId = targetMembershipId;}
    public void withRequestContext(String ipAddress, String userAgent){
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
    public void withDetails(String detailsJson){this.details = detailsJson;}

    public UUID getId() {return id;}
    public UUID getOrganizationId() {return organizationId;}
    public UUID getActorMembershipId() {return actorMembershipId;}
    public UUID getTargetMembershipId() {return targetMembershipId;}
    public String getAction() {return action;}
    public String getEntityType() {return entityType;}
    public UUID getEntityId() {return entityId;}
    public String getIpAddress() {return ipAddress;}
    public String getUserAgent() {return userAgent;}
    public String getDetails() {return details;}
    public Instant getHappenedAt() {return happenedAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof AuditLog other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
