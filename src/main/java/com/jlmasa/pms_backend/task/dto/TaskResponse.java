package com.jlmasa.pms_backend.task.dto;

import com.jlmasa.pms_backend.task.domain.TaskPriority;
import com.jlmasa.pms_backend.task.domain.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public record TaskResponse(UUID id, UUID projectId, String title, TaskStatus status, TaskPriority priority, Instant startAt, Instant dueAt, UUID milestoneId, UUID parentTaskId, Instant createdA) {
}
