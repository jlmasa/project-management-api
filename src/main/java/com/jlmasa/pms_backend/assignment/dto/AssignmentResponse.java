package com.jlmasa.pms_backend.assignment.dto;

import com.jlmasa.pms_backend.assignment.domain.AssignmentStatus;

import java.time.Instant;
import java.util.UUID;

public record AssignmentResponse(UUID id, UUID assignedToMembershipId, UUID assignedByMembershipId, AssignmentStatus status, Instant assignedAt, Instant endedAt) {
}
