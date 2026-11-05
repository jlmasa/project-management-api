package com.jlmasa.pms_backend.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MembershipRoleId implements Serializable {

    @Column(name = "organization_membership_id", nullable = false)
    private UUID organizationMembershipId;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    protected MembershipRoleId() {}

    public MembershipRoleId(UUID organizationMembershipId, UUID roleId) {
        this.organizationMembershipId = Objects.requireNonNull(organizationMembershipId);
        this.roleId = Objects.requireNonNull(roleId);
    }

    public UUID getOrganizationMembershipId() { return organizationMembershipId; }
    public UUID getRoleId() { return roleId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MembershipRoleId other)) return false;
        return organizationMembershipId.equals(other.organizationMembershipId)
                && roleId.equals(other.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationMembershipId, roleId);
    }
}