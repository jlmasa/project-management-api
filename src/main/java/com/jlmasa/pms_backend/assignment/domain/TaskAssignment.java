package com.jlmasa.pms_backend.assignment.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "task_assignments")
public class TaskAssignment {

    @Id @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "task_id", nullable = false, updatable = false)
    private UUID taskId;

    @Column(name = "assigned_to_membership_id", nullable = false, updatable = false)
    private UUID assignedToMembershipId;

    @Column(name = "assigned_by_membership_id", nullable = false, updatable = false)
    private UUID assignedByMembershipId;

    // Snapshot, not a live FK association: preserves the assignee's department AT THE TIME of assignment
    // (BR-14), even if the person later transfers department

    @Column(name = "department_id_at_assignment", updatable = false)
    private UUID departmentIdAtAssignment;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status", nullable = false, length = 20)
    private AssignmentStatus assignmentStatus;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;

    @Column(name = "first_started_at")
    private Instant firstStartedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "end_reason", length = 20)
    private AssignmentEndReason endReason;

    @Column(name = "created_at", nullable = false,updatable = false)
    private Instant createdAt;

    protected TaskAssignment(){}

    public TaskAssignment(UUID taskId, UUID assignedToMembershipId, UUID assignedByMembershipId, UUID departmentIdAtAssignment, Instant now){
        this.taskId = Objects.requireNonNull(taskId, "taskID");
        this.assignedToMembershipId = Objects.requireNonNull(assignedToMembershipId, "assignedToMembershipId");
        this.assignedByMembershipId = Objects.requireNonNull(assignedByMembershipId, "assignedByMembershipId");
        this.departmentIdAtAssignment = departmentIdAtAssignment;
        this.assignmentStatus = AssignmentStatus.ASSIGNED;
        this.assignedAt = now;
        this.createdAt = now;
    }

    public void recordFirstStart(Instant now){
        if(this.firstStartedAt == null) {
            this.firstStartedAt = now;
        }
        this.assignmentStatus = AssignmentStatus.IN_PROGRESS;
    }

    public void end(AssignmentEndReason reason, Instant now){
        if(this.endedAt != null){
            throw new IllegalStateException("Assignment "+id+ " has already ended.");
        }
        this.endReason = Objects.requireNonNull(reason, "reason");
        this.endedAt = now;
        this.assignmentStatus = switch(reason){
            case COMPLETED -> AssignmentStatus.COMPLETED;
            case CANCELLED, TASK_CLOSED ->  AssignmentStatus.CANCELLED;
            case REASSIGNED ->  AssignmentStatus.ASSIGNED;
        };

        if (reason == AssignmentEndReason.COMPLETED) {
            this.completedAt = now;
        }
    }

    public boolean hasEnded() {return endedAt != null;}

    public UUID getId() { return id; }
    public UUID getTaskId() { return taskId; }
    public UUID getAssignedToMembershipId() { return assignedToMembershipId; }
    public UUID getAssignedByMembershipId() { return assignedByMembershipId; }
    public UUID getDepartmentIdAtAssignment() { return departmentIdAtAssignment; }
    public AssignmentStatus getAssignmentStatus() { return assignmentStatus; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getFirstStartedAt() { return firstStartedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public Instant getEndedAt() { return endedAt; }
    public AssignmentEndReason getEndReason() { return endReason; }
    public Instant getCreatedAt() { return createdAt; }


    @Override
    public boolean equals(Object o){
        if (this ==o) return true;
        if(!(o instanceof TaskAssignment other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int  hashCode() {return getClass().hashCode();}
}

