package com.jlmasa.pms_backend.identity.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignRoleRequest(@NotNull UUID membershipId, @NotNull UUID roleId) {
}
