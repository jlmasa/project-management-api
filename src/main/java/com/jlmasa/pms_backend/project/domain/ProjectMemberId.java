package com.jlmasa.pms_backend.project.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProjectMemberId implements Serializable {

    private UUID projectId;
    private UUID organizationMembershipId;

    protected ProjectMemberId(){}

    public ProjectMemberId(UUID projectId, UUID organizationMembershipId){
        this.projectId = Objects.requireNonNull(projectId);
        this.organizationMembershipId = Objects.requireNonNull(organizationMembershipId);
    }
    public UUID getProjectId() {return projectId;}
    public UUID getOrganizationMembershipId() {return organizationMembershipId;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof ProjectMemberId other)) return false;
        return projectId.equals(other.projectId) && organizationMembershipId.equals(other.organizationMembershipId);
    }

    @Override
    public int hashCode() {return Objects.hash(projectId, organizationMembershipId);}
}
