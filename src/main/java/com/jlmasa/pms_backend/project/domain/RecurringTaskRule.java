package com.jlmasa.pms_backend.project.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "recurring_task_rules")
public class RecurringTaskRule {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private Project project;

    @Column(name = "created_by_membership_id", nullable = false, updatable = false)
    private UUID createdByMembershipId;

    @Column(name = "default_assignee_membership_id")
    private UUID defaultAssigneeMembershipId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "recurrence_rule", nullable = false)
    private String recurrenceRule;

    @Column(name = "default_priority", nullable = false, length = 20)
    private String defaultPriority;

    @Column(name = "next_run_at")
    private Instant nextRunAt;

    @Column(name = "last_generated_at")
    private Instant lastGeneratedAt;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected RecurringTaskRule(){}

    public RecurringTaskRule(Project project, UUID createdByMembershipId, String title, String recurrenceRule, Instant now) {
        this.project = Objects.requireNonNull(project, "project");
        this.createdByMembershipId = Objects.requireNonNull(createdByMembershipId, "createdByMembershipId");
        this.title = Objects.requireNonNull(title, "title");
        this.recurrenceRule = Objects.requireNonNull(recurrenceRule, "recurrenceRule");
        this.defaultPriority = "MEDIUM";
        this.active = true;
        this.createdAt = now;
        this.updatedAt = now;
    }
    public void deactivate(Instant now) {this.active = false; this.updatedAt=now;}

    public void recordGeneration(Instant generatedAt, Instant nextRunAt){
        this.lastGeneratedAt = generatedAt;
        this.nextRunAt = nextRunAt;
        this.updatedAt = generatedAt;
    }

    public UUID getId() {return id;}
    public Project getProject() {return project;}
    public UUID getCreatedByMembershipId() {return createdByMembershipId;}
    public UUID getDefaultAssigneeMembershipId() {return defaultAssigneeMembershipId;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public String getRecurrenceRule() {return recurrenceRule;}
    public String getDefaultPriority() {return defaultPriority;}
    public Instant getNextRunAt() {return nextRunAt;}
    public Instant getLastGeneratedAt() {return lastGeneratedAt;}
    public boolean isActive() {return active;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getUpdatedAt() {return updatedAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof RecurringTaskRule other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
