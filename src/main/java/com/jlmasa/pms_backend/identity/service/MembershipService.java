package com.jlmasa.pms_backend.identity.service;


import com.jlmasa.pms_backend.identity.domain.Department;
import com.jlmasa.pms_backend.identity.domain.Organization;
import com.jlmasa.pms_backend.identity.domain.OrganizationMembership;
import com.jlmasa.pms_backend.identity.domain.UserAccount;
import com.jlmasa.pms_backend.identity.repository.OrganizationMembershipRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Objects;
import java.util.Optional;

@Service
public class MembershipService {

    private final OrganizationMembershipRepository membershipRepository;
    private final Clock clock;

    public MembershipService(OrganizationMembershipRepository membershipRepository, Clock clock){
        this.membershipRepository = membershipRepository;
        this.clock = clock;
    }

    @Transactional
    public OrganizationMembership addMember(Organization organization, UserAccount user, Department department){
        Optional<OrganizationMembership> existing = membershipRepository.findActiveByUserAndOrganization(user.getId(), organization.getId());
        if(existing.isPresent()){
            throw new IllegalStateException("This user already has a membership in this organization");
        }
        Objects.requireNonNull(department, "A membership requires a department.");
        if(!department.getOrganization().getId().equals(organization.getId())){
            throw new IllegalArgumentException("That department does not belong to this organization.");
        }
        OrganizationMembership membership = new OrganizationMembership(organization, user, department,clock.instant());
        return membershipRepository.save(membership);
    }

    @Transactional
    public void changeDepartment(OrganizationMembership membership, Department newDepartment){
        Objects.requireNonNull(newDepartment, "A membership requires a department.");
        if(!newDepartment.getOrganization().getId().equals(membership.getOrganization().getId())){
            throw new IllegalArgumentException("That department does not belong to this membership's organization.");
        }
        membership.changeDepartment(newDepartment);
    }

    @Transactional
    public void suspend(OrganizationMembership membership){
        membership.suspend();
    }
    @Transactional
    public void reactivate(OrganizationMembership membership){
        membership.reactivate();
    }

    @Transactional
    public void leave(OrganizationMembership membership){
        membership.leave(clock.instant());
    }
}