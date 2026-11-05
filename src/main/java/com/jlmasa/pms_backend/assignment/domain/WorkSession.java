package com.jlmasa.pms_backend.assignment.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "work_sessions")
public class WorkSession {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "task_assignment_id", nullable = false, updatable = false)
    private UUID taskAssignmentId;

    @Column(name = "started_by_membership_id", nullable = false, updatable = false)
    private UUID startedByMembershipId;

    @Column(name = "ended_by_membership_id")
    private UUID endedByMembershipId;

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", nullable = false, length = 10)
    private SessionStatus sessionStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "end_reason", length = 20)
    private SessionEndReason endReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected WorkSession() {}

    public WorkSession(UUID taskAssignmentId, UUID startedByMembershipId, Instant now){
        this.taskAssignmentId = Objects.requireNonNull(taskAssignmentId,"taskAssignmentId");
        this.startedByMembershipId = Objects.requireNonNull(startedByMembershipId, "startedByMembershipId");
        this.startedAt = now;
        this.sessionStatus = SessionStatus.ACTIVE;
        this.createdAt = now;
    }
    public boolean isActive() {return sessionStatus == SessionStatus.ACTIVE;}

    public void end(UUID endedByMembershipId, SessionEndReason reason, Instant now){
        if(!isActive()){
            throw new IllegalStateException("Work session" +id+" is already ended.");
        }
        this.endedByMembershipId = Objects.requireNonNull(endedByMembershipId, "endedByMembershipId");
        this.endReason = Objects.requireNonNull(reason, "reason");
        this.endedAt = now;
        this.durationSeconds = Duration.between(startedAt, now).getSeconds();
        this.sessionStatus = SessionStatus.ENDED;
    }

    public UUID getId() {return id;}
    public UUID getTaskAssignmentId() {return taskAssignmentId;}
    public UUID getStartedByMembershipId() {return startedByMembershipId;}
    public UUID getEndedByMembershipId() {return endedByMembershipId;}
    public Instant getStartedAt(){return startedAt;}
    public Instant getEndedAt() {return endedAt;}
    public Long getDurationSeconds() {return durationSeconds;}
    public SessionStatus getSessionStatus() {return sessionStatus;}
    public SessionEndReason getEndReason() {return endReason;}
    public Instant getCreatedAt() {return createdAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof WorkSession other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
