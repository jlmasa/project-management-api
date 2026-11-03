package com.jlmasa.pms_backend.identity.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MembershipRoleId implements Serializable {

    private UUID organizationMembershipId;
    private UUID roleId;

    protected MembershipRoleId(){}

    public MembershipRoleId(UUID organizationMembershipId, UUID roleId){
        this.organizationMembershipId = Objects.requireNonNull(organizationMembershipId);
        this.roleId = Objects.requireNonNull(roleId);
    }
    public UUID getOrganizationMembershipId() {return organizationMembershipId;}
    public UUID getRoleId(){return roleId;}
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof MembershipRoleId other)) return false;
        return organizationMembershipId.equals(other.organizationMembershipId) && roleId.equals(other.roleId);
    }
    public int hashCode() {return Objects.hash(organizationMembershipId, roleId);}
}
