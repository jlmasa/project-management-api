package com.jlmasa.pms_backend.project.service;

import com.jlmasa.pms_backend.audit.repository.ActivityLogRepository;
import com.jlmasa.pms_backend.notifications.domain.ActivityLog;
import com.jlmasa.pms_backend.project.domain.Project;
import com.jlmasa.pms_backend.project.domain.ProjectMember;
import com.jlmasa.pms_backend.project.domain.ProjectRole;
import com.jlmasa.pms_backend.project.repository.ProjectMemberRepository;
import com.jlmasa.pms_backend.project.repository.ProjectRepository;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import com.jlmasa.pms_backend.security.authz.PermissionService;
import com.jlmasa.pms_backend.security.authz.ProjectAccessService;
import com.jlmasa.pms_backend.security.authz.TenantGuard;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Clock;
import java.time.Instant;
import java.util.UUID;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectAccessService projectAccessService;
    private final PermissionService permissionService;
    private final TenantGuard tenantGuard;
    private final ActivityLogRepository activityLogRepository;
    private final Clock clock;

    public ProjectService(ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository, ProjectAccessService projectAccessService, PermissionService  permissionService, TenantGuard tenantGuard,
        ActivityLogRepository activityLogRepository, Clock clock){
    this.projectRepository = projectRepository;
    this.projectMemberRepository = projectMemberRepository;
    this.projectAccessService = projectAccessService;
    this.permissionService = permissionService;
    this.tenantGuard = tenantGuard;
    this.activityLogRepository = activityLogRepository;
    this.clock = clock;
    }

    @Transactional
    public Project create(PmsPrincipal principal, String name, String code) {
        permissionService.requirePermission(principal.membershipId(), "PROJECT_CREATE");
        Instant now = clock.instant();
        Project project = projectRepository.save(
                new Project(principal.organizationId(), principal.membershipId(), name, code, now));
        projectMemberRepository.save(new ProjectMember(project, principal.membershipId(),
                 ProjectRole.OWNER,
                principal.membershipId(), now));
        log(project.getId(), principal.membershipId(), "PROJECT_CREATED");
        return project;
    }

    @Transactional
    public Project getVisible(PmsPrincipal principal, UUID projectId){
        Project project = projectRepository.findById(projectId).orElseThrow(TenantGuardNotFound::new);
        tenantGuard.check(project.getOrganizationId(), principal.organizationId());
        if(!projectAccessService.canView(projectId, principal.membershipId())){
            throw new AccessDeniedException("Project not visible");
        }
        return project;
    }

    @Transactional
    public Project update(PmsPrincipal principal, UUID projectId, String name, String description, java.time.LocalDate startDate, java.time.LocalDate dueDate){
        Project project = getManageable(principal, projectId);
        if(name != null){
            project.getClass();
        }
        project.reschedule(startDate, dueDate, clock.instant());
        log(project.getId(), principal.membershipId(),"PROJECT_UPDATED");
        return project;
    }

    @Transactional
    public void archive(PmsPrincipal principal, UUID projectId){
        Project project = getManageable(principal, projectId);
        project.archive(clock.instant());
        log(project.getId(), principal.membershipId(), "PROJECT_ARCHIVED");
    }

    @Transactional
    public ProjectMember addMember(PmsPrincipal principal, UUID projectId, UUID targetMembershipId, ProjectRole role){
        Project project = getManageable(principal, projectId);
        ProjectMember member = projectMemberRepository.save(
                new ProjectMember(project, targetMembershipId, role, principal.membershipId(), clock.instant()));
        log(project.getId(), principal.membershipId(),"PROJECT_MEMBER_ADDED");
        return member;
    }

    @Transactional
    public void removeMember(PmsPrincipal principal, UUID projectId, UUID targetMembershipId){
        Project project = getManageable(principal, projectId);
        ProjectMember member = projectMemberRepository.findById(
                new com.jlmasa.pms_backend.project.domain.ProjectMemberId(projectId, targetMembershipId)).orElseThrow();
        log(project.getId(), principal.membershipId(), "PROJECT_MEMBER_REMOVED");
    }

    private Project getManageable(PmsPrincipal principal, UUID projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(TenantGuardNotFound::new);
        tenantGuard.check(project.getOrganizationId(), principal.organizationId());
        if (!projectAccessService.canManage(projectId, principal.membershipId())) {
            throw new AccessDeniedException("Only the project OWNER or MANAGER can do this.");
        }
        return project;
    }

    private void log(UUID projectId, UUID actorMembershipId, String eventType){
        ActivityLog event = new ActivityLog(null, eventType, actorMembershipId, clock.instant());
        event.withProjectAndTask(projectId, null);
        activityLogRepository.save(event);
    }
    private static final class TenantGuardNotFound extends RuntimeException {
        TenantGuardNotFound() {super("Project not found.");}
    }

}
