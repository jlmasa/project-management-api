package com.jlmasa.pms_backend.project.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "project_members")
public class ProjectMember {

    @EmbeddedId
    private ProjectMemberId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private Project project;

    @Column(name = "organization_membership_id", insertable = false, updatable = false)
    private UUID organizationMembershipId;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role", nullable = false, length = 20)
    private ProjectRole projectRole;

    @Column(name = "added_by_membership_id")
    private UUID addedByMembershipId;

    @Column(name = "added_at", nullable = false, updatable = false)
    private Instant addedAt;

    @Column(name = "removed_at") private Instant removedAt;

    protected ProjectMember(){}

    public ProjectMember(Project project, UUID organizationMembershipId, ProjectRole projectRole, UUID addedByMembershipId, Instant now){
        this.project = Objects.requireNonNull(project, "project");
        this.organizationMembershipId = Objects.requireNonNull(organizationMembershipId, "organizationMembershipId");
        this.id = new ProjectMemberId(project.getId(), organizationMembershipId);
        this.projectRole = Objects.requireNonNull(projectRole, "projectRoles");
        this.addedByMembershipId = addedByMembershipId;
        this.addedAt = now;
    }

    public void remove(Instant now){this.removedAt = now;}
    public boolean isActive() {return removedAt == null;}
    public void changeRole(ProjectRole newRole) {this.projectRole = Objects.requireNonNull(newRole);}

    public ProjectMemberId getId() {return id;}
    public Project getProject() {return project;}
    public UUID getOrganizationMembershipId() {return organizationMembershipId;}
    public ProjectRole getProjectRole() {return projectRole;}
    public UUID getAddedByMembershipId() {return addedByMembershipId;}
    public Instant getAddedAt() {return addedAt;}
    public Instant getRemovedAt() {return removedAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof ProjectMember other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
