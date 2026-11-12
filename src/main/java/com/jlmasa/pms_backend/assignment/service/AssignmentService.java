package com.jlmasa.pms_backend.assignment.service;

import com.jlmasa.pms_backend.assignment.domain.AssignmentEndReason;
import com.jlmasa.pms_backend.assignment.domain.TaskAssignment;
import com.jlmasa.pms_backend.assignment.repository.TaskAssignmentRepository;
import com.jlmasa.pms_backend.audit.repository.ActivityLogRepository;
import com.jlmasa.pms_backend.identity.domain.MembershipStatus;
import com.jlmasa.pms_backend.identity.repository.OrganizationMembershipRepository;
import com.jlmasa.pms_backend.notifications.domain.ActivityLog;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import com.jlmasa.pms_backend.security.authz.PermissionService;
import com.jlmasa.pms_backend.security.authz.ProjectAccessService;
import com.jlmasa.pms_backend.task.domain.Task;
import com.jlmasa.pms_backend.task.domain.TaskStatus;
import com.jlmasa.pms_backend.task.repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
public class AssignmentService {

    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final ProjectAccessService projectAccessService;
    private final PermissionService permissionService;
    private final ActivityLogRepository activityLogRepository;
    private final Clock clock;
    private final WorkSessionService workSessionService;

    public AssignmentService(TaskRepository taskRepository, TaskAssignmentRepository taskAssignmentRepository, OrganizationMembershipRepository membershipRepository, ProjectAccessService projectAccessService,
                             PermissionService permissionService, ActivityLogRepository activityLogRepository, Clock clock, WorkSessionService workSessionService){
        this.taskRepository = taskRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.membershipRepository = membershipRepository;
        this.projectAccessService = projectAccessService;
        this.permissionService = permissionService;
        this.activityLogRepository = activityLogRepository;
        this.clock = clock;
        this.workSessionService = workSessionService;
    }

    @Transactional
    public TaskAssignment assign(PmsPrincipal principal, UUID taskId, UUID assigneeMembershipId){
        Task task = authorizeAssignment(principal,taskId);
        var assignee = membershipRepository.findById(assigneeMembershipId).orElseThrow();
        if(assignee.getMembershipStatus() != MembershipStatus.ACTIVE){
            throw new IllegalArgumentException("The assignee must be an active member");
        }
        if(taskAssignmentRepository.findByTaskIdAndEndedAtIsNull(taskId).isPresent()){
            throw new IllegalStateException("This task already has a current assignment; use reassign instead.");
        }
        TaskAssignment assignment = taskAssignmentRepository.save(new TaskAssignment(taskId, assigneeMembershipId, principal.membershipId(),
                assignee.getDepartment() != null ? assignee.getDepartment().getId() : null, clock.instant()));
        log(task,principal.membershipId(), "TASK_ASSIGNED", null, assigneeMembershipId);
        return assignment;
    }

    @Transactional
    public TaskAssignment reassign(PmsPrincipal principal, UUID taskId, UUID newAssigneeMembershipId){
        Task task = authorizeAssignment(principal, taskId);
        TaskAssignment current = taskAssignmentRepository.findByTaskIdAndEndedAtIsNull(taskId)
                .orElseThrow(() -> new IllegalStateException("No current assignment to reassign"));
        var newAssignee = membershipRepository.findById(newAssigneeMembershipId).orElseThrow();
        if (newAssignee.getMembershipStatus() != MembershipStatus.ACTIVE){
            throw new IllegalArgumentException("The new assignee must be an active member");
        }
        UUID previousAssignee = current.getAssignedToMembershipId();
        current.end(AssignmentEndReason.REASSIGNED, clock.instant());
        stopRunningWorkSessionForReassignment(current.getId());

        TaskAssignment next = taskAssignmentRepository.save(new TaskAssignment(
                taskId, newAssigneeMembershipId, principal.membershipId(),
                newAssignee.getDepartment() != null ? newAssignee.getDepartment().getId() : null, clock.instant()));
        log(task, principal.membershipId(), "TASK_REASSIGNED", previousAssignee, newAssigneeMembershipId);
        return next;
    }

    @Transactional(readOnly = true)
    public List<TaskAssignment> history(UUID taskId){
        return taskAssignmentRepository.findByTaskIdOrderByAssignedAtDesc(taskId);
    }

    private Task authorizeAssignment(PmsPrincipal principal, UUID taskId){
        Task task = taskRepository.findById(taskId).orElseThrow();
        if(task.getTaskStatus() == TaskStatus.COMPLETED || task.getTaskStatus() == TaskStatus.CANCELLED){
            throw new IllegalStateException("Cannot assign a task that is COMPLETE or CANCELLED");
        }
        boolean canManage  = projectAccessService.canManage(task.getProjectId(), principal.membershipId());
        if(!canManage || !permissionService.hasPermission(principal.membershipId(), "TASK_ASSIGN")){
            throw new AccessDeniedException("Only the project OWNER/MANGER with TASK_ASSIGN may assign task.");
        }
        return task;
    }

    private void stopRunningWorkSessionForReassignment(UUID endedAssignmentId){
        workSessionService.stopForAssignment(endedAssignmentId, null, clock.instant());
    }

    private void log(Task task, UUID actorMembershipId, String eventType, UUID oldAssignee, UUID newAssignee) {
        ActivityLog event = new ActivityLog(null, eventType, actorMembershipId, clock.instant());
        event.withProjectAndTask(task.getProjectId(), task.getId());
        event.withValues(
                oldAssignee != null ? "{\"assignee\":\"" + oldAssignee + "\"}" : null,
                "{\"assignee\":\"" + newAssignee + "\"}", null);
        activityLogRepository.save(event);
    }
}
