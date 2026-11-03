package com.jlmasa.pms_backend.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(name = "project_id", updatable = false)
    private UUID projectId;

    @Column(name = "task_id", updatable = false)
    private UUID taskId;

    @Column(name = "task_assignment_id", updatable = false)
    private UUID taskAssignmentId;

    @Column(name = "work_session_id", updatable = false)
    private UUID workSessionId;

    @Column(name = "actor_membership_id", updatable = false)
    private UUID actorMembershipId;

    @Column(name = "target_membership_id", updatable = false)
    private UUID targetMembershipId;


    @Column(name = "event_type", nullable = false, updatable = false, length = 50)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_values", updatable = false)
    private String oldValues;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_values", updatable = false)
    private String newValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", updatable = false)
    private String details;

    @Column(name = "happened_at", nullable = false, updatable = false)
    private Instant happenedAt;

    protected ActivityLog(){}

    public ActivityLog(UUID organizationId, String eventType, UUID actorMembershipId, Instant now) {
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId");
        this.eventType = Objects.requireNonNull(eventType, "eventType");
        this.actorMembershipId = actorMembershipId;
        this.happenedAt = now;
    }

    public void withProjectAndTask(UUID projectId, UUID taskId) {this.projectId = projectId; this.taskId = taskId;}
    public void withAssignmentContext(UUID taskAssignmentId, UUID workSessionId){
        this.taskAssignmentId = taskAssignmentId;
        this.workSessionId = workSessionId;
    }
    public void withTarget(UUID targetMembershipId) {this.targetMembershipId = targetMembershipId;}
    public void withValues(String oldValuesJson, String newValuesJson, String detailsJson){
        this.oldValues = oldValuesJson;
        this.newValue = newValuesJson;
        this.details = detailsJson;
    }

    public UUID getId() {return id;}
    public UUID getOrganizationId() {return organizationId;}
    public UUID getProjectId() {return projectId;}
    public UUID getTaskId() {return taskId;}
    public UUID getTaskAssignmentId() {return taskAssignmentId;}
    public UUID getWorkSessionId() {return workSessionId;}
    public UUID getActorMembershipId() {return actorMembershipId;}
    public UUID getTargetMembershipId() {return targetMembershipId;}
    public String getEventType() {return eventType;}
    public String getOldValues() {return oldValues;}
    public String getNewValue() {return newValue;}
    public String getDetails() {return details;}
    public Instant getHappenedAt() {return happenedAt;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof ActivityLog other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
