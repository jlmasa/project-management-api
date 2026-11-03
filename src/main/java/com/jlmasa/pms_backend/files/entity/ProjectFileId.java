package com.jlmasa.pms_backend.files.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProjectFileId implements Serializable {

    private UUID projectId;
    private UUID fileId;

    protected ProjectFileId(){}

    public ProjectFileId(UUID projectId, UUID fileId){
        this.projectId = Objects.requireNonNull(projectId);
        this.fileId = Objects.requireNonNull(fileId);
    }

    public UUID getProjectId(){return projectId;}
    public UUID getFileId() {return fileId;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof ProjectFileId other)) return false;
        return projectId.equals(other.projectId) && fileId.equals(other.fileId);
    }
    @Override
    public int hashCode(){return Objects.hash(projectId, fileId);}
}
