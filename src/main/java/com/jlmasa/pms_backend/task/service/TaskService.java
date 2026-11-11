package com.jlmasa.pms_backend.task.service;

import com.jlmasa.pms_backend.audit.repository.ActivityLogRepository;
import com.jlmasa.pms_backend.notifications.domain.ActivityLog;
import com.jlmasa.pms_backend.project.domain.Milestone;
import com.jlmasa.pms_backend.project.domain.Project;
import com.jlmasa.pms_backend.project.domain.ProjectRole;
import com.jlmasa.pms_backend.project.repository.MilestoneRepository;
import com.jlmasa.pms_backend.project.repository.ProjectRepository;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import com.jlmasa.pms_backend.security.authz.PermissionService;
import com.jlmasa.pms_backend.security.authz.ProjectAccessService;
import com.jlmasa.pms_backend.security.authz.TenantGuard;
import com.jlmasa.pms_backend.task.domain.Task;
import com.jlmasa.pms_backend.task.repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Clock;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final ProjectAccessService projectAccessService;
    private final PermissionService permissionService;
    private final TenantGuard tenantGuard;
    private ActivityLogRepository activityLogRepository;
    private final Clock clock;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository,
                       MilestoneRepository milestoneRepository, ProjectAccessService projectAccessService,
                       PermissionService permissionService, TenantGuard tenantGuard,
                       ActivityLogRepository activityLogRepository, Clock clock){
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.milestoneRepository = milestoneRepository;
        this.projectAccessService = projectAccessService;
        this.permissionService = permissionService;
        this.tenantGuard = tenantGuard;
        this.activityLogRepository = activityLogRepository;
        this.clock = clock;
    }

    @Transactional
    public Task create(PmsPrincipal principal, UUID projectId, String title, java.time.Instant startAt, java.time.Instant dueAt, UUID milestoneId, UUID parentTaskId){
        Project project = loadProjectForWrite(principal, projectId, "TASK_CREATE");
        if(project.getProjectStatus() == com.jlmasa.pms_backend.project.domain.ProjectStatus.ARCHIVED){
            throw new IllegalStateException("Cannot create a task in an archived project.");
        }
        if(dueAt != null && startAt != null && dueAt.isBefore(startAt)){
            throw new IllegalArgumentException("dueAt must not be before startAt");
        }
        if(milestoneId != null){
            Milestone milestone = milestoneRepository.findById(milestoneId).orElseThrow();
            if(!milestone.getProject().getId().equals(projectId)){
                throw new IllegalArgumentException("milestoneId must belong to the same project.");
            }
        }
        Task parent = null;
        if (parentTaskId != null){
            parent = taskRepository.findById(parentTaskId).orElseThrow();
            if(!parent.getProjectId().equals(projectId)){
                throw new IllegalArgumentException("parentTaskId must belong to the same project");
            }
        }
        Task task = new Task(projectId, principal.membershipId(), title, clock.instant());
        if(milestoneId != null) task.assignToMilestone(milestoneId, clock.instant());
        task = taskRepository.save(task);
        log(task.getId(), projectId, principal.membershipId(),"TASK_CREATED");
        return task;
    }

    @Transactional(readOnly = true)
    public Task getVisible(PmsPrincipal principal, UUID taskId){
        Task task = taskRepository.findById(taskId).orElseThrow(TaskNotFound::new);
        Project project = projectRepository.findById(task.getProjectId()).orElseThrow(TaskNotFound::new);
        tenantGuard.check(project.getOrganizationId(), principal.organizationId());
        if(!projectAccessService.canView(project.getId(), principal.membershipId())){
            throw new AccessDeniedException("Task not visible");
        }
        return task;
    }

    @Transactional
    public Task update(PmsPrincipal principal, UUID taskId, String title, String description, String acceptanceCriteria, java.time.Instant startAt, java.time.Instant dueAt){
        Task task = getVisible(principal, taskId);
        Project project = projectRepository.findById(task.getProjectId()).orElseThrow();
        Boolean isManager = projectAccessService.canManage(project.getId(), principal.membershipId());
        Boolean isAssignee = false;

        if(!isManager && !isAssignee){
            throw new AccessDeniedException("Only the project OWNER/MANAGER or the current assignee may edit this task");
        }
        if(dueAt != null && startAt != null && dueAt.isBefore(startAt)){
            throw new IllegalArgumentException("dueAt must not be before startAt");
        }
        task.moveTo(task.getTaskStatus(), clock.instant());
        log(task.getId(), task.getProjectId(), principal.membershipId(), "TASK_UPDATED");
        return task;
    }

    @Transactional(readOnly = true)
    public com.jlmasa.pms_backend.common.PageResponse<Task> list(PmsPrincipal principal,
                                                                 UUID projectId, com.jlmasa.pms_backend.task.dto.TaskFilterRequest filter,
                                                                 org.springframework.data.domain.Pageable pageable){
        Project project = projectRepository.findById(projectId).orElseThrow(TaskNotFound::new);
        tenantGuard.check(project.getOrganizationId(), principal.organizationId());
        if(!projectAccessService.canView(projectId, principal.membershipId())){
            throw new AccessDeniedException("Project not visible");
        }
        var page = filter.status() != null ? taskRepository.findByProjectIdAndTaskStatus(projectId, filter.status(), pageable) :
                new org.springframework.data.domain.PageImpl<>(taskRepository.findByProjectId(projectId));
        return com.jlmasa.pms_backend.common.PageResponse.from(page, java.util.function.Function.identity());
    }

    private Project loadProjectForWrite(PmsPrincipal principal, UUID projectId, String alsoRequiredPermission){
        Project project = projectRepository.findById(projectId).orElseThrow(TaskNotFound::new);
        tenantGuard.check(project.getOrganizationId(), principal.organizationId());
        boolean roleAllows = projectAccessService.activeRole(projectId,principal.membershipId())
                .map(role -> role == ProjectRole.OWNER || role == ProjectRole.MANAGER || role == ProjectRole.MEMBER).orElse(false);
        if(!roleAllows || !permissionService.hasPermission(principal.membershipId(), alsoRequiredPermission)){
            throw new AccessDeniedException("Missing project role or " + alsoRequiredPermission + ".");
        }
        return project;
    }

    private void log(UUID taskId, UUID projectId, UUID actorMembershipId, String eventType){
        ActivityLog event = new ActivityLog(null, eventType, actorMembershipId, clock.instant());
        event.withProjectAndTask(projectId, taskId);
        activityLogRepository.save(event);
    }

    private static final class TaskNotFound extends RuntimeException {
        TaskNotFound() {super("Task not found.");}
    }


}
