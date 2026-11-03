package com.jlmasa.pms_backend.files.domain;

import com.jlmasa.project_management.api.identity.entity.Organization;
import com.jlmasa.project_management.api.identity.entity.Organization_memberships;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "files")
public class Files {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id", nullable = false) private UUID id;
    @Id @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false) private Organization organization;
    @Column(name = "title") private String title;
    @Id @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "owner_membership_id", nullable = false) private Organization_memberships organization_memberships;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant created_at;

    @Column(name = "updated_at", nullable = false)
    private Instant updated_at;

    @Column(name = "deleted_at")
    private Instant deleted_at;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Organization_memberships getOrganization_memberships() {
        return organization_memberships;
    }

    public void setOrganization_memberships(Organization_memberships organization_memberships) {
        this.organization_memberships = organization_memberships;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }

    public Instant getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Instant updated_at) {
        this.updated_at = updated_at;
    }

    public Instant getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Instant deleted_at) {
        this.deleted_at = deleted_at;
    }
}
