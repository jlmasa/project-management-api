package com.jlmasa.pms_backend.assignment.service;

import com.jlmasa.pms_backend.assignment.domain.SessionStatus;
import com.jlmasa.pms_backend.assignment.domain.WorkSession;
import com.jlmasa.pms_backend.assignment.dto.ManHoursReportRow;
import com.jlmasa.pms_backend.assignment.repository.TaskAssignmentRepository;
import com.jlmasa.pms_backend.assignment.repository.WorkSessionRepository;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import com.jlmasa.pms_backend.security.authz.PermissionService;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.UUID;

@Service
public class ManHoursReportService {

    private static final ZoneId MANILA = ZoneId.of("Asia/Manila");

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final WorkSessionRepository workSessionRepository;
    private final TimeAdjustmentService timeAdjustmentService;
    private final PermissionService permissionService;
    private final Clock clock;

    public ManHoursReportService(TaskAssignmentRepository taskAssignmentRepository, WorkSessionRepository workSessionRepository,
                                 TimeAdjustmentService timeAdjustmentService, PermissionService permissionService, Clock clock){
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.workSessionRepository = workSessionRepository;
        this.timeAdjustmentService = timeAdjustmentService;
        this.permissionService = permissionService;
        this.clock = clock;
    }

    public Instant[] toUtcRange(LocalDate from, LocalDate to){
        Instant start = from.atStartOfDay(MANILA).toInstant();
        Instant endExclusive = to.plusDays(1).atStartOfDay(MANILA).toInstant();
        return new Instant[] {start, endExclusive};
    }

    @Transactional(readOnly = true)
    public List<ManHoursReportRow> report (PmsPrincipal principal, UUID employeeMembershipId, UUID departmentId,
                                           UUID projectId, LocalDate from, LocalDate to){
        applyScope(principal, employeeMembershipId, departmentId, projectId);
        Instant[] range = toUtcRange(from, to);

        List<com.jlmasa.pms_backend.assignment.domain.TaskAssignment> assignments =
                taskAssignmentRepository.findByAssignedToMembershipIdAndAssignedAtBetween(employeeMembershipId, range[0], range[1]);

        return assignments.stream().map(assignment -> {
            long clippedSeconds = clippedSecondsForAssignment(assignment.getId(), range[0], range[1]);
            return new ManHoursReportRow(assignment.getId(), assignment.getAssignedToMembershipId(),
                    assignment.getDepartmentIdAtAssignment(), null, assignment.getTaskId(),
                    assignment.getAssignedAt(), assignment.getFirstStartedAt(), assignment.getCompletedAt(),
                    clippedSeconds, round2(clippedSeconds / 3600.0), from + " to " +to+ "(Asia/Manila)");
        }).toList();
    }

    long clippedSecondsForAssignment(UUID assignmentId, Instant rangeStart, Instant rangeEndExclusive){
        List<WorkSession> sessions =
                workSessionRepository.findByTaskAssignmentIdOrderByStartedAtAsc(assignmentId);
        long total = 0;
        for( WorkSession session : sessions){
            Instant sessionStart = session.getStartedAt();
            Instant sessionEnd = session.getSessionStatus() == SessionStatus.ACTIVE ? clock.instant() : session.getEndedAt();
            Instant overlapStart = sessionStart.isAfter(rangeStart) ? sessionStart : rangeStart;
            Instant overlapEnd = sessionEnd.isBefore(rangeEndExclusive) ? sessionEnd : rangeEndExclusive;
            if(overlapEnd.isAfter(overlapStart)){
                long sessionSeconds = Duration.between(overlapStart, overlapEnd).getSeconds();
                total += sessionSeconds;
            }
        }
        return total;
    }

    private void applyScope(PmsPrincipal principal, UUID employeeMembershipId, UUID departmentId, UUID projectId){
        boolean isSelf = employeeMembershipId != null &&
                employeeMembershipId.equals(principal.membershipId());
        if(isSelf && permissionService.hasPermission(principal.membershipId(),"REPORT_VIEW_OWN")){
            return;
        }
        if(departmentId != null && permissionService.hasPermission(principal.membershipId(), "REPORT_VIEW_DEPARTMENT")){
            return;
        }
        if (permissionService.hasPermission(principal.membershipId(),"REPORT_VIEW_ORG")){
            return;
        }
        throw new org.springframework.security.access.AccessDeniedException("No report scope permits this query.");
    }
    private static double round2(double value){
        return Math.round(value *100.0)/100.0;
    }
}
