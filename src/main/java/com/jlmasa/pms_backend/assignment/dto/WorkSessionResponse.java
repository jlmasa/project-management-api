package com.jlmasa.pms_backend.assignment.dto;

import com.jlmasa.pms_backend.assignment.domain.SessionStatus;

import java.time.Instant;
import java.util.UUID;

public record WorkSessionResponse(UUID id, UUID taskAssignmentId, SessionStatus status, Instant startedAt, Instant endedAt, Long durationSeconds, long runningTotalSeconds) {
}
