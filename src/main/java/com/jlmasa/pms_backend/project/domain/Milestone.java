package com.jlmasa.pms_backend.project.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "milestones")
public class Milestone {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private Project project;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "milestone_status", nullable = false, length = 20)
    private MilestoneStatus milestoneStatus;

    @Column(name = "due_at")
    private Instant dueAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_by_membership_id", nullable = false, updatable = false)
    private UUID createdByMembershipId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Milestone() {}

    public Milestone(Project project, String name, UUID createdByMembershipId, Instant now){
        this.project = Objects.requireNonNull(project, "project");
        this.name = Objects.requireNonNull(name, "name");
        this.createdByMembershipId = Objects.requireNonNull(createdByMembershipId, "createdByMembershipId");
        this.milestoneStatus = MilestoneStatus.OPEN;
        this.createdAt = now;
    }

    public void complete(Instant now) {
        this.milestoneStatus = MilestoneStatus.COMPLETED;
        this.completedAt = now;
    }

    public void cancel() { this.milestoneStatus = MilestoneStatus.CANCELLED;}

    public UUID getId() {return id;}
    public Project getProject() {return project;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public MilestoneStatus getMilestoneStatus() {return milestoneStatus;}
    public Instant getDueAt() {return dueAt;}
    public Instant getCompletedAt() {return completedAt;}
    public UUID getCreatedByMembershipId() {return createdByMembershipId;}
    public Instant getCreatedAt() {return createdAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof Milestone other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
