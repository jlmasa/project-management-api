package com.jlmasa.pms_backend.task.domain;

import com.jlmasa.project_management.api.identity.entity.Organization_memberships;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "recurring_task_rules")
public class Recurring_task_rules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_membership_id", nullable = false)
    private Organization_memberships createdByMembership;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "default_assignee_membership_id")
    private Organization_memberships defaultAssigneeMembership;

    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "recurrence_rule", nullable = false)
    private String recurrence_rule;
    @Column(name = "default_priority", nullable = false)
    private String default_priority;


    @Column(name = "next_run_at")
    private Instant next_run_at;

    @Column(name = "last_generated_at")
    private Instant last_generated_at;

    @Column(name = "is_active", nullable = false) private Boolean is_active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant created_at;

    @Column(name = "updated_at")
    private Instant updated_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Organization_memberships getCreatedByMembership() {
        return createdByMembership;
    }

    public void setCreatedByMembership(Organization_memberships createdByMembership) {
        this.createdByMembership = createdByMembership;
    }

    public Organization_memberships getDefaultAssigneeMembership() {
        return defaultAssigneeMembership;
    }

    public void setDefaultAssigneeMembership(Organization_memberships defaultAssigneeMembership) {
        this.defaultAssigneeMembership = defaultAssigneeMembership;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecurrence_rule() {
        return recurrence_rule;
    }

    public void setRecurrence_rule(String recurrence_rule) {
        this.recurrence_rule = recurrence_rule;
    }

    public String getDefault_priority() {
        return default_priority;
    }

    public void setDefault_priority(String default_priority) {
        this.default_priority = default_priority;
    }

    public Instant getNext_run_at() {
        return next_run_at;
    }

    public void setNext_run_at(Instant next_run_at) {
        this.next_run_at = next_run_at;
    }

    public Instant getLast_generated_at() {
        return last_generated_at;
    }

    public void setLast_generated_at(Instant last_generated_at) {
        this.last_generated_at = last_generated_at;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
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
}
