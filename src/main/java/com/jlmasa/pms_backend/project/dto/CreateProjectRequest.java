package com.jlmasa.pms_backend.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(@NotBlank @Size(max = 255) String name, @Size(max = 50) String code) {
}
