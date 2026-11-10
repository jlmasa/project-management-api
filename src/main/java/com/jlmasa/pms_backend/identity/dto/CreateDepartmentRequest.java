package com.jlmasa.pms_backend.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDepartmentRequest(@NotBlank @Size(max = 255) String name, @NotBlank @Size(max = 50) String code) {
}
