package com.jlmasa.pms_backend.task.service;

import com.jlmasa.pms_backend.assignment.domain.AssignmentEndReason;
import com.jlmasa.pms_backend.assignment.domain.SessionEndReason;
import com.jlmasa.pms_backend.assignment.repository.TaskAssignmentRepository;
import com.jlmasa.pms_backend.assignment.service.WorkSessionService;
import com.jlmasa.pms_backend.audit.repository.ActivityLogRepository;
import com.jlmasa.pms_backend.audit.repository.AuditLogRepository;
import com.jlmasa.pms_backend.notifications.domain.ActivityLog;
import com.jlmasa.pms_backend.task.domain.Task;
import com.jlmasa.pms_backend.task.domain.TaskStatus;
import com.jlmasa.pms_backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TaskLifecycleService {


    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final ActivityLogRepository activityLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final Clock clock;
    private final WorkSessionService workSessionService;

    public TaskLifecycleService(TaskRepository taskRepository, TaskAssignmentRepository taskAssignmentRepository, ActivityLogRepository activityLogRepository,
                                AuditLogRepository auditLogRepository, Clock clock, WorkSessionService workSessionService){
        this.taskRepository = taskRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.activityLogRepository = activityLogRepository;
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
        this.workSessionService = workSessionService;
    }

    @Transactional
    public void close(Task task, TaskStatus closingStatus, UUID actorMembershipId){
        Instant now = clock.instant();
        TaskStatus previousStatus = task.getTaskStatus();
        List<com.jlmasa.pms_backend.assignment.domain.TaskAssignment> openAssignments  = taskAssignmentRepository.findByTaskIdAndEndedAtIsNull(task.getId()).stream().toList();

        if(closingStatus == TaskStatus.COMPLETED){
            task.complete(now);
        }else {
            task.cancel(now);
        }

        stopActiveWorkSessionForClosure(openAssignments,actorMembershipId, now);
        AssignmentEndReason assignmentReason = closingStatus == TaskStatus.COMPLETED ? AssignmentEndReason.COMPLETED : AssignmentEndReason.TASK_CLOSED;
        openAssignments.forEach(assignment -> assignment.end(assignmentReason, now));
        closeCurrentTaskConversation(task.getId(), now);

        ActivityLog activity = new ActivityLog(null, "TASK_" + closingStatus, actorMembershipId,now);
        activity.withProjectAndTask(task.getProjectId(), task.getId());
        activity.withValues("{\"status\":\"" +previousStatus + "\"}", "{\"status\":\""+closingStatus+ "\"}", null);
        activityLogRepository.save(activity);
        registerAfterCommitNotification(task.getId(), closingStatus);
    }

    @Transactional
    public void reopen(Task task, UUID actorMembershipId){
        Instant now = clock.instant();
        TaskStatus previousStatus = task.getTaskStatus();
        task.reopen(now);
        startNextTaskConversationCycle(task.getId());
        ActivityLog activity = new ActivityLog(null, "TASK_REOPENED", actorMembershipId, now);
        activity.withProjectAndTask(task.getProjectId(), task.getId());
        activity.withValues("{\"status\":\"" + previousStatus + "\"}", "{\"status\":\"TO_DO\"}", null);
        activityLogRepository.save(activity);
    }

    private void stopActiveWorkSessionForClosure(
            List<com.jlmasa.pms_backend.assignment.domain.TaskAssignment> assignments, UUID actorMembershipId, Instant now){
        com.jlmasa.pms_backend.assignment.domain.SessionEndReason reason = SessionEndReason.COMPLETED;
        workSessionService.stopAllForTask(assignments, reason, actorMembershipId, now);
    }

    private void closeCurrentTaskConversation(UUID taskId, Instant now){
    }

    private void startNextTaskConversationCycle(UUID taskId){
    }

    private void registerAfterCommitNotification(UUID taskId, TaskStatus closingStatus){
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
            public void afterCommit(){
        }
        });
    }

}
