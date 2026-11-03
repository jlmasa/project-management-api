package com.jlmasa.pms_backend.identity.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "organization_memberships")
public class OrganizationMembership {

    @Id @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id") private UUID id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "employee_code")
    private String employeeCode;

    @Enumerated(EnumType.STRING) @Column(name = "membership_status", nullable = false, length = 20) private MembershipStatus membershipStatus;

    @Column(name = "joined_at", nullable = false, updatable = false) private Instant joinedAt;

    @Column(name = "left_at") private Instant leftAt;

    protected OrganizationMembership(){}

    public OrganizationMembership(Organization organization, UserAccount user, Department department, Instant now){
        this.organization = Objects.requireNonNull(organization, "organization");
        this.user = Objects.requireNonNull(user, "user");
        this.department = department;
        this.membershipStatus = MembershipStatus.ACTIVE;
        this.joinedAt = now;
    }

    public void suspend() {this.membershipStatus = MembershipStatus.SUSPENDED;}

    public void reactivate() {
        if(this.membershipStatus == MembershipStatus.LEFT){
            throw new IllegalStateException("A LEFT membership must be re-invited, not reactivated.");
        }
        this.membershipStatus = MembershipStatus.ACTIVE;
    }

    public void leave(Instant now){
        this.membershipStatus = MembershipStatus.LEFT;
        this.leftAt = now;
    }

    public UUID getId(){return id;}
    public Organization getOrganization() {return organization;}
    public UserAccount getUser() {return user;}
    public Department getDepartment() {return department;}
    public String getEmployeeCode() {return employeeCode;}
    public MembershipStatus getMembershipStatus() {return membershipStatus;}
    public Instant getJoinedAt() {return joinedAt;}
    public Instant getLeftAt() {return leftAt;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof OrganizationMembership other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
