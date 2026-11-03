package com.jlmasa.pms_backend.timetracking.entity;

import com.jlmasa.project_management.api.identity.entity.Departments;
import com.jlmasa.project_management.api.identity.entity.Organization_memberships;
import com.jlmasa.project_management.api.task.entity.Tasks;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "task_assignments")
public class Task_assignments {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Tasks tasks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_membership_id", nullable = false)
    private Organization_memberships assignToMembership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_membership_id", nullable = false)
    private Organization_memberships assignByMembership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id_at_assignment")
    private Departments departments;

    @Column(name = "assignment_status", nullable = false) private String assignment_status;
    @Column(name = "assigned_at", nullable = false) private Instant assigned_at;
    @Column(name = "first_started_at") private Instant first_started_at;
    @Column(name = "completed_at") private Instant completed_at;
    @Column(name = "ended_at") private Instant ended_at;
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

    public Departments getDepartments() {
        return departments;
    }

    public void setDepartments(Departments departments) {
        this.departments = departments;
    }

    public String getAssignment_status() {
        return assignment_status;
    }

    public void setAssignment_status(String assignment_status) {
        this.assignment_status = assignment_status;
    }

    public Instant getAssigned_at() {
        return assigned_at;
    }

    public void setAssigned_at(Instant assigned_at) {
        this.assigned_at = assigned_at;
    }

    public Instant getFirst_started_at() {
        return first_started_at;
    }

    public void setFirst_started_at(Instant first_started_at) {
        this.first_started_at = first_started_at;
    }

    public Instant getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(Instant completed_at) {
        this.completed_at = completed_at;
    }

    public Instant getEnded_at() {
        return ended_at;
    }

    public void setEnded_at(Instant ended_at) {
        this.ended_at = ended_at;
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
