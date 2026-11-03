package com.jlmasa.pms_backend.task.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "task_dependencies")
public class TaskDependency {

    @EmbeddedId
    private TaskDependencyId id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("taskId")
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("dependsOnTaskId")
    @JoinColumn(name = "depends_on_task_id", nullable = false)
    private Task dependsOnTask;

    @Column(name = "dependency_type", nullable = false) private  String dependencyType;

    @Column(name = "created_by_membership_id", nullable = false, updatable = false) private UUID createdByMembershipId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected TaskDependency(){}

    public TaskDependency(Task task, Task dependsOnTask, UUID createdByMembershipId, Instant now){
        if(Objects.equals(task.getId(), dependsOnTask.getId())){
            throw new IllegalArgumentException("A task cannot depend on itself");
        }
        this.task = Objects.requireNonNull(task, "task");
        this.dependsOnTask = Objects.requireNonNull(dependsOnTask, "dependsOnTask");
        this.id = new TaskDependencyId(task.getId(), dependsOnTask.getId());
        this.dependencyType = "FINISH_TO_START";
        this.createdByMembershipId = Objects.requireNonNull(createdByMembershipId, "createdByMembershipId");
        this.createdAt =  now;
    }

    public TaskDependencyId getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public Task getDependsOnTask() {
        return dependsOnTask;
    }

    public String getDependencyType() {
        return dependencyType;
    }

    public UUID getCreatedByMembershipId() {
        return createdByMembershipId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o){
        if (this ==o) return true;
        if(!(o instanceof TaskDependency other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
