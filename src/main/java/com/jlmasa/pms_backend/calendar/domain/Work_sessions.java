package com.jlmasa.pms_backend.calendar.domain;

import com.jlmasa.project_management.api.identity.entity.Departments;
import com.jlmasa.project_management.api.identity.entity.Organization_memberships;
import com.jlmasa.project_management.api.task.entity.Tasks;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "work_sessions")
public class Work_sessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_assignment_id", nullable = false)
    private Tasks tasks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "started_by_membership_id", nullable = false)
    private Organization_memberships assignToMembership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ended_by_membership_id", nullable = false)
    private Organization_memberships assignByMembership;

    @Column(name = "started_at", nullable = false) private Instant started_at;
    @Column(name = "ended_at") private Instant ended_at;

    @Column(name = "duration_seconds") private BigInteger duration_seconds;
    @Column(name = "session_status", nullable = false) private String session_status;
    @Column(name = "end_reason") private String end_reason;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Tasks getTasks() {
        return tasks;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }

    public Organization_memberships getAssignToMembership() {
        return assignToMembership;
    }

    public void setAssignToMembership(Organization_memberships assignToMembership) {
        this.assignToMembership = assignToMembership;
    }

    public Organization_memberships getAssignByMembership() {
        return assignByMembership;
    }

    public void setAssignByMembership(Organization_memberships assignByMembership) {
        this.assignByMembership = assignByMembership;
    }

    public Instant getStarted_at() {
        return started_at;
    }

    public void setStarted_at(Instant started_at) {
        this.started_at = started_at;
    }

    public Instant getEnded_at() {
        return ended_at;
    }

    public void setEnded_at(Instant ended_at) {
        this.ended_at = ended_at;
    }

    public BigInteger getDuration_seconds() {
        return duration_seconds;
    }

    public void setDuration_seconds(BigInteger duration_seconds) {
        this.duration_seconds = duration_seconds;
    }

    public String getSession_status() {
        return session_status;
    }

    public void setSession_status(String session_status) {
        this.session_status = session_status;
    }

    public String getEnd_reason() {
        return end_reason;
    }

    public void setEnd_reason(String end_reason) {
        this.end_reason = end_reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
