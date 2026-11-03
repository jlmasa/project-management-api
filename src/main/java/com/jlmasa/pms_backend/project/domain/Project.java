package com.jlmasa.pms_backend.project.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(name = "created_by_membership_id", nullable = false, updatable = false)
    private UUID createdByMembershipId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false, length = 20)
    private ProjectStatus projectStatus;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private Instant completeAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "archived_at")
    private Instant archivedAt;

    protected Project(){}

    public Project(UUID organizationId, UUID createdByMembershipId,String name, String code, Instant now){
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId");
        this.createdByMembershipId = Objects.requireNonNull(createdByMembershipId, "createdByMembershipId");
        this.name = Objects.requireNonNull(name,"name");
        this.code  = code;
        this.projectStatus = ProjectStatus.PLANNING;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void activate(Instant now){
        this.projectStatus = ProjectStatus.ACTIVE;
        this.updatedAt = now;
    }

    public void complete(Instant now){
        this.projectStatus = ProjectStatus.COMPLETED;
        this.completeAt = now;
        this.updatedAt = now;
    }
    public void archive(Instant now){
        this.projectStatus = ProjectStatus.ARCHIVED;
        this.archivedAt = now;
        this.updatedAt = now;
    }

    public void reschedule(LocalDate startDate, LocalDate dueDate, Instant now){
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.updatedAt = now;
    }

    public UUID getId() {return id;}
    public UUID getOrganizationId() {return organizationId;}
    public UUID getCreatedByMembershipId() {return createdByMembershipId;}
    public String getName() {return name;}
    public String getCode() {return code;}
    public String getDescription() {return description;}
    public ProjectStatus getProjectStatus() {return projectStatus;}
    public LocalDate getStartDate() {return startDate;}
    public LocalDate getDueDate() {return dueDate;}
    public Instant getCompleteAt() {return completeAt;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getUpdatedAt() {return updatedAt;}
    public Instant getArchivedAt() {return archivedAt;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Project other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
