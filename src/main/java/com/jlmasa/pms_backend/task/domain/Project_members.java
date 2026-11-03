package com.jlmasa.pms_backend.task.domain;


import com.jlmasa.project_management.api.identity.entity.Organization_memberships;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "project_members")
public class Project_members {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Projects projects;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_membership_id", nullable = false)
    private Organization_memberships organization_memberships;

    @Column(name = "project_role", nullable = false) private String project_role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "added_by_membership_id")
    private Organization_memberships addedByMembership;

    @Column(name = "added_at")
    private Instant added_at;

    @Column(name = "removed_at")
    private Instant removed_at;

    public Projects getProjects() {
        return projects;
    }

    public void setProjects(Projects projects) {
        this.projects = projects;
    }

    public Organization_memberships getOrganization_memberships() {
        return organization_memberships;
    }

    public void setOrganization_memberships(Organization_memberships organization_memberships) {
        this.organization_memberships = organization_memberships;
    }

    public Instant getAdded_at() {
        return added_at;
    }

    public void setAdded_at(Instant added_at) {
        this.added_at = added_at;
    }

    public Instant getRemoved_at() {
        return removed_at;
    }

    public void setRemoved_at(Instant removed_at) {
        this.removed_at = removed_at;
    }

    public String getProject_role() {
        return project_role;
    }

    public void setProject_role(String project_role) {
        this.project_role = project_role;
    }
}
