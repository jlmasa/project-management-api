package com.jlmasa.pms_backend.integration.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "integrations")
public class Integration {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20, updatable = false)
    private IntegrationProvider provider;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "secret_reference")
    private String secretReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "integration_status", nullable = false, length = 10)
    private IntegrationStatus integrationStatus;

    @Column(name = "created_by_membership_id", nullable = false, updatable = false)
    private UUID createdByMembershipId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Integration() {}

    public Integration(UUID organizationId, IntegrationProvider provider, String displayName, UUID createdByMembershipId, Instant now){
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId");
        this.provider = Objects.requireNonNull(provider, "provider");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.integrationStatus = IntegrationStatus.ACTIVE;
        this.createdByMembershipId = Objects.requireNonNull(createdByMembershipId, "createdByMembershipId");
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {return id;}
    public UUID getOrganizationId() {return organizationId;}
    public IntegrationProvider getProvider() {return provider;}
    public String getDisplayName() {return displayName;}
    public String getSecretReference() {return secretReference;}
    public IntegrationStatus getIntegrationStatus() {return integrationStatus;}
    public UUID getCreatedByMembershipId() {return createdByMembershipId;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getUpdatedAt() {return updatedAt;}

    @Override
    public boolean equals(Object o){
        if (this ==o) return true;
        if(!(o instanceof Integration other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
