package com.jlmasa.pms_backend.task.domain;

import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

public record TaskBoardRow(UUID id, String title, TaskStatus taskStatus, TaskPriority priority, Instant dueAt) {
}
