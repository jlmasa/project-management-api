package com.jlmasa.pms_backend.assignment.dto;

import java.time.Instant;
import java.util.UUID;

public record ManHoursReportRow(UUID assignmentId, UUID employeeMembershipId, UUID departmentId, UUID projectId,
                                UUID taskId, Instant assignedAt, Instant firstStartedAt,  Instant completedAt,
                                long totalSeconds, double totalHours, String rangeEchoManila) {
}
