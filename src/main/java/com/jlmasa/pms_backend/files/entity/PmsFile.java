package com.jlmasa.pms_backend.files.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "files")
public class PmsFile {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "owner_membership_id", nullable = false, updatable = false)
    private UUID ownerMembershipId;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_scope", nullable = false, length = 20)
    private AccessScope accessScope;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected PmsFile(){}

    public PmsFile(UUID organizationId, String title, UUID ownerMembershipId, Instant now){
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId");
        this.title = Objects.requireNonNull(title, "title");
        this.ownerMembershipId = Objects.requireNonNull(ownerMembershipId, "owerMembershipId");
        this.accessScope = AccessScope.PROJECT;
        this.createdAt = now;
        this.updatedAt = now;
    }
    public void rename(String newTitle, Instant now){this.title = Objects.requireNonNull(newTitle); this.updatedAt = now;}
    public void changeScope(AccessScope scope, Instant now){this.accessScope = Objects.requireNonNull(scope); this.updatedAt = now;}
    public void softDelete(Instant now){this.deletedAt = now; this.updatedAt = now;}
    public boolean isDeleted() {return deletedAt != null;}

    public UUID getId() {return id;}
    public UUID getOrganizationId() {return organizationId;}
    public String getTitle() {return title;}
    public UUID getOwnerMembershipId() {return ownerMembershipId;}
    public AccessScope getAccessScope() {return accessScope;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getUpdatedAt() {return updatedAt;}
    public Instant getDeletedAt() {return deletedAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof PmsFile other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
