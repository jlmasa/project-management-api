package com.jlmasa.pms_backend.project.dto;

import com.jlmasa.pms_backend.project.domain.ProjectStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse(UUID id, String name, String code, ProjectStatus status, LocalDate startDate, LocalDate dueDate, Instant createdAt) {
}
