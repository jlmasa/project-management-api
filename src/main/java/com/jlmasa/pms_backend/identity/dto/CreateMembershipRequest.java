package com.jlmasa.pms_backend.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateMembershipRequest(@NotBlank @Email String email, @NotBlank String displayName, @NotBlank String temporaryPassword, UUID departmentId) {
}
