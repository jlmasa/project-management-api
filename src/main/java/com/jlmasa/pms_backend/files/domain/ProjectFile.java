package com.jlmasa.pms_backend.files.domain;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name= "project_files")
public class ProjectFile {

    @EmbeddedId
    private ProjectFileId id;

    @Column(name = "project_id", insertable = false, updatable = false)
    private UUID projectId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("fileId")
    @JoinColumn(name = "file_id", nullable = false, updatable = false)
    private PmsFile file;

    @Column(name = "folder_path")
    private String folderPath;

    @Column(name = "linked_by_membership_id", nullable = false, updatable = false)
    private UUID linkedByMembershipId;

    @Column(name = "linked_at", nullable = false, updatable = false)
    private Instant linkedAt;

    protected ProjectFile(){}

    public ProjectFile(UUID projectId, PmsFile file, String folderPath, UUID linkedByMembershipId, Instant now){
        this.projectId = Objects.requireNonNull(projectId, "projectId");
        this.file = Objects.requireNonNull(file, "file");
        this.id = new ProjectFileId(projectId, file.getId());
        this.folderPath = folderPath;
        this.linkedByMembershipId = Objects.requireNonNull(linkedByMembershipId, "linkedByMembershipId");
        this.linkedAt = now;
    }

    public ProjectFileId getId() {return id;}
    public UUID getProjectId() {return projectId;}
    public PmsFile getFile(){return file;}
    public String getFolderPath() {return folderPath;}
    public UUID getLinkedByMembershipId() {return linkedByMembershipId;}
    public Instant getLinkedAt() {return linkedAt;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof ProjectFile other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
