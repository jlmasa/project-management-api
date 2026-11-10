package com.jlmasa.pms_backend.identity.bootstrap;

import com.jlmasa.pms_backend.audit.repository.AuditLogRepository;
import com.jlmasa.pms_backend.identity.domain.Department;
import com.jlmasa.pms_backend.identity.domain.Organization;
import com.jlmasa.pms_backend.identity.domain.OrganizationMembership;
import com.jlmasa.pms_backend.identity.domain.Role;
import com.jlmasa.pms_backend.identity.domain.UserAccount;
import com.jlmasa.pms_backend.identity.repository.*;
import com.jlmasa.pms_backend.identity.service.AccountService;
import com.jlmasa.pms_backend.identity.service.MembershipService;
import com.jlmasa.pms_backend.notifications.domain.AuditLog;
import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;

@Component
@EnableConfigurationProperties(BootstrapProperties.class)
public class FirstAdminInitializer implements ApplicationRunner {

    private final UserAccountRepository userAccountRepository;
    private final OrganizationRepository organizationRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final MembershipRoleRepository membershipRoleRepository;
    private final AccountService accountService;
    private final MembershipService membershipService;
    private final AuditLogRepository auditLogRepository;
    private final BootstrapProperties properties;
    private final Clock clock;

    public FirstAdminInitializer(UserAccountRepository userAccountRepository, OrganizationRepository organizationRepository,
                                 DepartmentRepository departmentRepository, RoleRepository roleRepository, MembershipRoleRepository membershipRoleRepository,
                                 AccountService accountService, MembershipService membershipService,
                                 AuditLogRepository auditLogRepository, BootstrapProperties properties, Clock clock){
        this.userAccountRepository = userAccountRepository;
        this.organizationRepository = organizationRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.membershipRoleRepository = membershipRoleRepository;
        this.accountService = accountService;
        this.membershipService = membershipService;
        this.auditLogRepository = auditLogRepository;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args){
        if(userAccountRepository.count() > 0 || !properties.isComplete()){
            return;
        }

        Instant now  = clock.instant();
        Organization organization = organizationRepository.save(
                new Organization(properties.orgName(), slugify(properties.orgName()),now));

        Department adminDepartment = departmentRepository.save(
                new Department(organization, "Administration", "ADMIN", now));

        Role orgAdminRole = roleRepository.save(
                new Role(organization, "ORG_ADMIN", "Full Administrative access", true, now));

        UserAccount admin = accountService.createAccount(
                properties.adminEmail(), "Administrator", properties.adminPassword());

        OrganizationMembership membership = membershipService.addMember(organization, admin, adminDepartment);
        membershipRoleRepository.save(new com.jlmasa.pms_backend.identity.domain.MembershipRole(membership,orgAdminRole, null, now));

        AuditLog event = new AuditLog(organization.getId(), "FIRST ADMIN CREATED", "organizations", organization.getId(), now);
        event.withActor(membership.getId());
        event.withDetails("{\"adminEmail\":\""+admin.getEmail() +"\"}");
        auditLogRepository.save(event);
    }
    private static String slugify(String name){
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]+","-").replaceAll("(^-|-$)","");
    }
}