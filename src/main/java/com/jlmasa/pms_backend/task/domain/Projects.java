package com.jlmasa.pms_backend.task.domain;


import com.jlmasa.project_management.api.identity.entity.Organization;
import com.jlmasa.project_management.api.identity.entity.Organization_memberships;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "projects")
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="created_by_membership_id", nullable = false)
    private Organization_memberships organization_memberships;

    @Column(name = "name", nullable = false) private String name;
    @Column(name = "code") private String code;
    @Column(name = "description") private String description;
    @Column(name = "project_status", nullable = false) private String project_status;
    @Column(name = "start_date") private Date start_date;
    @Column(name = "due_date") private Date due_date;

    @Column(name = "completed_at")
    private Instant completed_at;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant created_at;

    @Column(name = "updated_at", nullable = false, updatable = true)
    private Instant updated_at;

    @Column(name = "archived_at")
    private Instant archived_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization_memberships getOrganization_memberships() {
        return organization_memberships;
    }

    public void setOrganization_memberships(Organization_memberships organization_memberships) {
        this.organization_memberships = organization_memberships;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProject_status() {
        return project_status;
    }

    public void setProject_status(String project_status) {
        this.project_status = project_status;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    public Instant getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(Instant completed_at) {
        this.completed_at = completed_at;
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

    public Instant getArchived_at() {
        return archived_at;
    }

    public void setArchived_at(Instant archived_at) {
        this.archived_at = archived_at;
    }
}
