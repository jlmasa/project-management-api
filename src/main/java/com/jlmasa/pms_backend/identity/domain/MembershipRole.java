package com.jlmasa.pms_backend.identity.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "membership_roles")
public class MembershipRole {

    @EmbeddedId
    private MembershipRoleId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_membership_id", nullable = false,
            insertable = false, updatable = false)
    private OrganizationMembership membership;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false,
            insertable = false, updatable = false)
    private Role role;

    @Column(name = "assigned_by_membership_id")
    private java.util.UUID assignedByMembershipId;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;

    protected MembershipRole(){}
    public MembershipRole(OrganizationMembership membership, Role role, java.util.UUID assignedByMembershipId, Instant now){
        this.membership = Objects.requireNonNull(membership, "membership");
        this.role = Objects.requireNonNull(role, "role");
        this.id = new MembershipRoleId(membership.getId(), role.getId());
        this.assignedByMembershipId = assignedByMembershipId;
        this.assignedAt = now;
    }

    public MembershipRoleId getId() {return id;}
    public OrganizationMembership getMembership() { return membership; }
    public Role getRole() {return role;}
    public java.util.UUID getAssignedByMembershipId() {return assignedByMembershipId;}
    public Instant getAssignedAt() {return assignedAt;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof MembershipRole other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
