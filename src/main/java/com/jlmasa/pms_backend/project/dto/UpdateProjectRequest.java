package com.jlmasa.pms_backend.project.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProjectRequest(@Size(max = 255) String name, String description, LocalDate startDate, LocalDate dueDate) {
}
