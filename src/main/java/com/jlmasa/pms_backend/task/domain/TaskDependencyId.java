package com.jlmasa.pms_backend.task.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class TaskDependencyId implements Serializable {

    private UUID taskId;
    private UUID dependsOnTaskId;

    protected TaskDependencyId(){}

    public TaskDependencyId(UUID taskId, UUID dependsOnTaskId){
        this.taskId = Objects.requireNonNull(taskId);
        this.dependsOnTaskId = Objects.requireNonNull(dependsOnTaskId);
    }
    public UUID getTaskId(){return taskId;}
    public UUID getDependsOnTaskId() {return dependsOnTaskId;}

    @Override
    public boolean equals(Object o){
        if(this==o)return true;
        if(!(o instanceof TaskDependencyId other)) return false;
        return taskId.equals(other.taskId) && dependsOnTaskId.equals(other.dependsOnTaskId);
    }
    @Override
    public int hashCode() {return Objects.hash(taskId, dependsOnTaskId);}
}
