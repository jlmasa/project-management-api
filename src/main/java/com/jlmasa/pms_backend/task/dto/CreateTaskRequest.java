package com.jlmasa.pms_backend.task.dto;

import com.jlmasa.pms_backend.task.domain.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 10000) String description,
        TaskPriority priority,
        Instant startAt,
        Instant dueAt,
        @Positive Integer estimateMinutes,
        UUID milestonesId
) {
}
