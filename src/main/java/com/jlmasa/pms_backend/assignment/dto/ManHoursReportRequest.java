package com.jlmasa.pms_backend.assignment.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ManHoursReportRequest(LocalDate from, LocalDate to, UUID employeeMembershipId, UUID departmentId, UUID projectId ) {
}
