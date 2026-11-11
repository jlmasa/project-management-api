package com.jlmasa.pms_backend.task.dto;

import com.jlmasa.pms_backend.task.domain.TaskPriority;
import com.jlmasa.pms_backend.task.domain.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public record TaskFilterRequest(TaskStatus status, TaskPriority priority, UUID assignmentMembershipId, Instant dueFrom, Instant dueTo, String titleContains) {
}
