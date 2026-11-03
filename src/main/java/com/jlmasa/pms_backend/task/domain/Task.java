package com.jlmasa.pms_backend.task.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id")
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "milestone_id")
    private UUID milestoneId;

    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @Column(name = "recurring_rule_id")
    private UUID recurringRuleId;

    @Column(name = "created_by_membership_id", nullable = false, updatable = false)
    private UUID createdByMembershipId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "acceptance_criteria", nullable = false)
    private String acceptanceCriteria;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false, length = 20)
    private TaskStatus taskStatus;

    @Enumerated(EnumType.STRING)
    @Column(name ="priority", nullable = false, length = 20)
    private TaskPriority priority;

    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "due_at")
    private Instant dueAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Task() {}

    public Task(UUID projectId, UUID createdByMembershipId, String title, Instant now){
        this.projectId = Objects.requireNonNull(projectId, "projectId");
        this.createdByMembershipId = Objects.requireNonNull(createdByMembershipId, "createdByMembershipId");
        this.title = Objects.requireNonNull(title, "title");
        this.taskStatus = TaskStatus.BACKLOG;
        this.priority = TaskPriority.MEDIUM;
        this.createdAt = now;
        this.updatedAt = now;
    }
    public void moveTo(TaskStatus newStatus, Instant now){
        this.taskStatus = Objects.requireNonNull(newStatus, "newStatus");
        this.updatedAt = now;
    }

    public void complete(Instant now){
        moveTo(TaskStatus.COMPLETED, now);
        this.completedAt = now;
    }

    public void cancel(Instant now){
        moveTo(TaskStatus.CANCELLED, now);
    }
    public void reopen(Instant now){
        this.completedAt = null;
        this.cancelledAt = null;
        moveTo(TaskStatus.TO_DO,now);
    }
    public void assignToMilestone(UUID milestoneId, Instant now) {this.milestoneId = milestoneId; this.updatedAt = now;}
    public void reprioritize(TaskPriority priority, Instant now){
        this.priority = Objects.requireNonNull(priority);
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getMilestoneId() {
        return milestoneId;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public UUID getRecurringRuleId() {
        return recurringRuleId;
    }

    public UUID getCreatedByMembershipId() {
        return createdByMembershipId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Task other)) return false;
        return id !=null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
