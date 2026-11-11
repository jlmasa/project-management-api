package com.jlmasa.pms_backend.assignment.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignTaskRequest(@NotNull UUID assigneeMembershipId) {
}
