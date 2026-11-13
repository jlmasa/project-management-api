package com.jlmasa.pms_backend.assignment.service;

import com.jlmasa.pms_backend.assignment.domain.SessionStatus;
import com.jlmasa.pms_backend.assignment.domain.TimeAdjustment;
import com.jlmasa.pms_backend.assignment.domain.WorkSession;
import com.jlmasa.pms_backend.assignment.repository.TimeAdjustmentRepository;
import com.jlmasa.pms_backend.assignment.repository.WorkSessionRepository;
import com.jlmasa.pms_backend.audit.repository.ActivityLogRepository;
import com.jlmasa.pms_backend.audit.repository.AuditLogRepository;
import com.jlmasa.pms_backend.notifications.domain.ActivityLog;
import com.jlmasa.pms_backend.notifications.domain.AuditLog;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import com.jlmasa.pms_backend.security.authz.PermissionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Clock;
import java.util.UUID;

@Service
public class TimeAdjustmentService {

    private static final int MAX_ABS_ADJUSTMENT_SECONDS = 24 * 60 * 60;

    private final WorkSessionRepository workSessionRepository;
    private final TimeAdjustmentRepository timeAdjustmentRepository;
    private final PermissionService permissionService;
    private final ActivityLogRepository activityLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final Clock clock;

    public TimeAdjustmentService(WorkSessionRepository workSessionRepository, TimeAdjustmentRepository timeAdjustmentRepository,
                                 PermissionService permissionService, ActivityLogRepository activityLogRepository,
                                 AuditLogRepository auditLogRepository, Clock clock){
        this.workSessionRepository = workSessionRepository;
        this.timeAdjustmentRepository = timeAdjustmentRepository;
        this.permissionService = permissionService;
        this.activityLogRepository = activityLogRepository;
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
    }

    @Transactional
    public TimeAdjustment create(PmsPrincipal principal, UUID workSessionId, int adjustmentSeconds, String reason){
        permissionService.requirePermission(principal.membershipId(), "TIME_ADJUST");

        if (adjustmentSeconds == 0 || Math.abs(adjustmentSeconds) > MAX_ABS_ADJUSTMENT_SECONDS){
            throw new IllegalArgumentException("adjustmentSeconds must be non-zero and within 24 hours");
        }
        WorkSession session = workSessionRepository.findById(workSessionId).orElseThrow();
        if(session.getSessionStatus() != SessionStatus.ENDED){
            throw new IllegalStateException("Only ENDED sessions can be adjusted; stop the timer first.");
        }

        long effectiveBefore = effectiveDuration(session);
        long effectiveAfter = effectiveBefore + adjustmentSeconds;
        if (effectiveAfter < 0){
            throw new IllegalArgumentException("This adjustment would take the sessions's duration below zero.");
        }

        if(principal.membershipId().equals(session.getStartedByMembershipId()) || principal.membershipId().equals(session.getEndedByMembershipId())){
            throw new AccessDeniedException("A member cannot adjust their own work session's time.");
        }
        TimeAdjustment adjustment = timeAdjustmentRepository.save(new TimeAdjustment(workSessionId, adjustmentSeconds, effectiveBefore, reason, principal.membershipId(), clock.instant()));

        ActivityLog activity = new ActivityLog(null, "TIME_ADJUSTED", principal.membershipId(), clock.instant());
        activity.withAssignmentContext(session.getTaskAssignmentId(), workSessionId);
        activity.withValues("{\"durationSeconds\":" + effectiveBefore + "}",
                "{\"durationSeconds\":" + effectiveAfter + "}", "{\"reason\":\"" + reason + "\"}");
        activityLogRepository.save(activity);

        AuditLog audit = new AuditLog(principal.organizationId(), "TIME_ADJUST_USED", "work_sessions", workSessionId, clock.instant());
        audit.withActor(principal.membershipId());
        auditLogRepository.save(audit);

        return adjustment;
    }

    public long effectiveDuration(WorkSession session){
        long original = session.getDurationSeconds() == null ? 0 : session.getDurationSeconds();
        long adjustment = timeAdjustmentRepository.findByWorkSessionIdOrderByCorrectedAtAsc(session.getId()).stream().mapToLong(TimeAdjustment::getAdjustmentSeconds).sum();
        return original + adjustment;
    }
}
