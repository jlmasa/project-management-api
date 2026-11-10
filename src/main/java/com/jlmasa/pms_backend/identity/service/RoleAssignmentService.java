package com.jlmasa.pms_backend.identity.service;

import com.jlmasa.pms_backend.identity.domain.Department;
import com.jlmasa.pms_backend.identity.domain.Organization;
import com.jlmasa.pms_backend.identity.domain.Role;
import com.jlmasa.pms_backend.identity.repository.DepartmentRepository;
import com.jlmasa.pms_backend.identity.repository.MembershipRoleRepository;
import com.jlmasa.pms_backend.identity.repository.OrganizationRepository;
import com.jlmasa.pms_backend.identity.repository.RoleRepository;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.UUID;

@Service
public class RoleAssignmentService {

    private final OrganizationRepository organizationRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final MembershipRoleRepository membershipRoleRepository;
    private final Clock clock;

    public RoleAssignmentService(OrganizationRepository organizationRepository, DepartmentRepository departmentRepository, RoleRepository roleRepository, MembershipRoleRepository membershipRoleRepository, Clock clock){
        this.organizationRepository = organizationRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.membershipRoleRepository = membershipRoleRepository;
        this.clock = clock;
    }

    public Organization organizationOf(PmsPrincipal principal){
        return organizationRepository.findById(principal.organizationId()).orElseThrow();
    }

    public Department departmentOf(UUID departmentId){
        return departmentRepository.findById(departmentId).orElseThrow();
    }

    @Transactional
    public void assign(PmsPrincipal actingPrincipal, UUID targetMembershipId, UUID roleId){
        if(targetMembershipId.equals(actingPrincipal.membershipId())){
            throw new AccessDeniedException("A member cannot assign a role to themself.");
        }
        Role role = roleRepository.findById(roleId).orElseThrow();
        var membership = new OrganizationMembershipLookup();
    }

    private static final class OrganizationMembershipLookup{}
}
