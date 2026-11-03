package com.jlmasa.pms_backend.task.domain;


import com.jlmasa.project_management.api.identity.entity.Organization_memberships;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "milestones")
public class Milestones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Projects projects;

    @Column(name = "name", nullable = false) private String name;
    @Column(name = "description") private String description;
    @Column(name = "milestone_status", nullable = false) private String milestone_status;

    @Column(name = "due_at")
    private Instant due_at;

    @Column(name = "completed_at")
    private Instant completed_at;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_membership_id", nullable = false)
    private Organization_memberships organization_memberships;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant created_at;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Projects getProjects() {
        return projects;
    }

    public void setProjects(Projects projects) {
        this.projects = projects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMilestone_status() {
        return milestone_status;
    }

    public void setMilestone_status(String milestone_status) {
        this.milestone_status = milestone_status;
    }

    public Instant getDue_at() {
        return due_at;
    }

    public void setDue_at(Instant due_at) {
        this.due_at = due_at;
    }

    public Instant getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(Instant completed_at) {
        this.completed_at = completed_at;
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
}
