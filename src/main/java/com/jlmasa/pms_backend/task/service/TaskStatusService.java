package com.jlmasa.pms_backend.task.service;

import com.jlmasa.pms_backend.audit.repository.ActivityLogRepository;
import com.jlmasa.pms_backend.notifications.domain.ActivityLog;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import com.jlmasa.pms_backend.security.authz.ProjectAccessService;
import com.jlmasa.pms_backend.task.domain.Task;
import com.jlmasa.pms_backend.task.domain.TaskStatus;
import com.jlmasa.pms_backend.task.repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class TaskStatusService {

    private final TaskRepository taskRepository;
    private final ProjectAccessService projectAccessService;
    private final TaskLifecycleService taskLifecycleService;
    private final ActivityLogRepository activityLogRepository;
    private final Clock clock;

    public TaskStatusService(TaskRepository taskRepository, ProjectAccessService projectAccessService,
                             TaskLifecycleService taskLifecycleService, ActivityLogRepository activityLogRepository, Clock clock){
        this.taskRepository = taskRepository;
        this.projectAccessService = projectAccessService;
        this.taskLifecycleService = taskLifecycleService;
        this.activityLogRepository = activityLogRepository;
        this.clock = clock;
    }

    @Transactional
    public Task transition(PmsPrincipal principal, UUID taskId, TaskStatus targetStatus){
        Task task = taskRepository.findById(taskId).orElseThrow();
        TaskStatus currentStatus = task.getTaskStatus();

        boolean isReopen = TaskTransitionTable.isReopen(currentStatus, targetStatus);
        if(!TaskTransitionTable.isAllowed(currentStatus, targetStatus)){
            throw new InvalidTaskTransitionException(currentStatus, targetStatus);
        }
        authorizeTransition(principal, task, targetStatus, isReopen);

        if (targetStatus == TaskStatus.IN_PROGRESS){

        }
        if(TaskTransitionTable.isClosing(targetStatus)){
            taskLifecycleService.close(task, targetStatus, principal.membershipId());
        }else if (isReopen){
            taskLifecycleService.reopen(task, principal.membershipId());
        }else {
            TaskStatus previous = task.getTaskStatus();
            task.moveTo(targetStatus, clock.instant());
            logTransition(task, previous, targetStatus, principal.membershipId());
        }
        return task;
    }

    private void authorizeTransition(PmsPrincipal principal, Task task, TaskStatus targetStatus, boolean isReopen){
        boolean canManage = projectAccessService.canManage(task.getProjectId(), principal.membershipId());
        if(isReopen){
            return;
        }
        if(TaskTransitionTable.isClosing(targetStatus) && !canManage){
            throw new AccessDeniedException("Only the project OWNER/MANAGER (or an org permission) may close a task.");
        }
    }

    private void logTransition(Task task, TaskStatus from, TaskStatus to, UUID actorMembershipId){
        ActivityLog event = new ActivityLog(null, "TASK_STATUS_CHANGED", actorMembershipId, clock.instant());
        event.withProjectAndTask(task.getProjectId(), task.getId());
        event.withValues("{\"status\":\"" + from + "\"}", "{\"status\":\"" + to + "\"}", null);
        activityLogRepository.save(event);
    }
}
