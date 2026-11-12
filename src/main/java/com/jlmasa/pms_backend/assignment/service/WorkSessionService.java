package com.jlmasa.pms_backend.assignment.service;

import com.jlmasa.pms_backend.assignment.domain.SessionEndReason;
import com.jlmasa.pms_backend.assignment.domain.SessionStatus;
import com.jlmasa.pms_backend.assignment.domain.TaskAssignment;
import com.jlmasa.pms_backend.assignment.domain.WorkSession;
import com.jlmasa.pms_backend.assignment.repository.TaskAssignmentRepository;
import com.jlmasa.pms_backend.assignment.repository.WorkSessionRepository;
import com.jlmasa.pms_backend.audit.repository.ActivityLogRepository;
import com.jlmasa.pms_backend.notifications.domain.ActivityLog;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import com.jlmasa.pms_backend.security.authz.ProjectAccessService;
import com.jlmasa.pms_backend.task.domain.Task;
import com.jlmasa.pms_backend.task.domain.TaskStatus;
import com.jlmasa.pms_backend.task.repository.TaskRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class WorkSessionService {

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final WorkSessionRepository workSessionRepository;
    private final TaskRepository taskRepository;
    private final ProjectAccessService projectAccessService;
    private ActivityLogRepository activityLogRepository;
    private final Clock clock;

    public WorkSessionService(TaskAssignmentRepository taskAssignmentRepository, WorkSessionRepository workSessionRepository,
                              TaskRepository taskRepository, ProjectAccessService projectAccessService,
                              ActivityLogRepository activityLogRepository, Clock clock){
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.workSessionRepository = workSessionRepository;
        this.taskRepository = taskRepository;
        this.projectAccessService = projectAccessService;
        this.activityLogRepository = activityLogRepository;
        this.clock = clock;
    }

    @Transactional
    public WorkSession start(PmsPrincipal principal, UUID assignmentId){
        TaskAssignment assignment = loadForOperation(principal, assignmentId);
        Task task = taskRepository.findById(assignment.getId()).orElseThrow();
        if(task.getTaskStatus() != TaskStatus.TO_DO && task.getTaskStatus() != TaskStatus.IN_PROGRESS){
            throw new InvalidTimerOperationException("Cannot start work: task status does not allow it.");
        }
        if (workSessionRepository.findByTaskAssignmentIdAndSessionStatus(assignmentId, SessionStatus.ACTIVE).isPresent()){
            throw new InvalidTimerOperationException("A session is already running for this assignment");
        }
        Instant now = clock.instant();
        WorkSession session;
        try {
            session = workSessionRepository.save(new WorkSession(assignmentId, principal.membershipId(), now));
        } catch (DataIntegrityViolationException ex){
            throw new InvalidTimerOperationException("A session is already running for this assignment.");
        }
        assignment.recordFirstStart(now);
        log(assignment, "WORK_STARTED", session.getId(), principal.membershipId());
        return session;
    }

    @Transactional
    public WorkSession pause(PmsPrincipal principal, UUID assignmentId){
        TaskAssignment assignment = loadForOperation(principal, assignmentId);
        WorkSession session = workSessionRepository
                .findByTaskAssignmentIdAndSessionStatus(assignmentId, SessionStatus.ACTIVE)
                .orElseThrow(() -> new InvalidTimerOperationException("No active session to pause."));
        session.end(principal.membershipId(), SessionEndReason.PAUSED, clock.instant());
        log(assignment, "WORK_PAUSED", session.getId(), principal.membershipId());
        return session;
    }

    @Transactional
    public WorkSession resume(PmsPrincipal principal, UUID assignmentId){
        TaskAssignment assignment = loadForOperation(principal, assignmentId);
        if (workSessionRepository.findByTaskAssignmentIdAndSessionStatus(assignmentId, SessionStatus.ACTIVE). isPresent()){
            throw new InvalidTimerOperationException("A session is already running; cannot resume");
        }
        var mostRecent = workSessionRepository.findByTaskAssignmentIdOrderByStartedAtAsc(assignmentId);
        if(mostRecent.isEmpty() || mostRecent.get(mostRecent.size() - 1).getEndReason() != SessionEndReason.PAUSED){
            throw new InvalidTimerOperationException("The last session must have ended with PAUSE to resume.");
        }
        Instant now = clock.instant();
        WorkSession session;
        try {
            session = workSessionRepository.save(new WorkSession(assignmentId, principal.membershipId(), now));
        } catch (DataIntegrityViolationException ex){
            throw new InvalidTimerOperationException("A session is already running; cannot resume.");
        }
        log(assignment, "WORK_RESUMED", session.getId(), principal.membershipId());
        return session;
    }

    @Transactional
    public WorkSession stop(PmsPrincipal principal, UUID assignmentId){
        TaskAssignment assignment = loadForOperation(principal, assignmentId);
        WorkSession session = workSessionRepository
                .findByTaskAssignmentIdAndSessionStatus(assignmentId, SessionStatus.ACTIVE)
                .orElseThrow(() -> new InvalidTimerOperationException("No active session to stop."));
        session.end(principal.membershipId(), SessionEndReason.USER_STOPPED, clock.instant());
        log(assignment, "WORK_STOPPED", session.getId(), principal.membershipId());
        return session;
    }

    public long runningTotalSeconds(UUID assignmentId){
        long ended =
                workSessionRepository.findByTaskAssignmentIdOrderByStartedAtAsc(assignmentId).stream()
                        .filter(s -> s.getSessionStatus() == SessionStatus.ENDED)
                        .mapToLong(s -> s.getDurationSeconds() == null ? 0 : s.getDurationSeconds())
                        .sum();
        long running = workSessionRepository.findByTaskAssignmentIdAndSessionStatus(assignmentId, SessionStatus.ACTIVE)
                .map(s -> java.time.Duration.between(s.getStartedAt(), clock.instant()).getSeconds()).orElse(0L);
        return ended + running;
    }

    private TaskAssignment loadForOperation(PmsPrincipal principal, UUID assignmentId){
        TaskAssignment assignment = taskAssignmentRepository.findByIdForUpdate(assignmentId).orElseThrow();
        if(assignment.hasEnded()){
            throw new InvalidTimerOperationException("This assignment has ended; no timer operations are allowed."  );
        }
        boolean isAssignee = assignment.getAssignedToMembershipId().equals(principal.membershipId());
        boolean isManager = false;
        if(!isAssignee && !isManager){
            throw new AccessDeniedException("Only the assignee or a project manager may operate this timer.");
        }
        return assignment;
    }

    private void log(TaskAssignment assignment, String eventType, UUID sessionId, UUID actorMembershipId){
        ActivityLog event = new ActivityLog(null, eventType, actorMembershipId, clock.instant());
        event.withProjectAndTask(null, assignment.getTaskId());
        event.withAssignmentContext(assignment.getId(), sessionId);
        activityLogRepository.save(event);
    }
}

