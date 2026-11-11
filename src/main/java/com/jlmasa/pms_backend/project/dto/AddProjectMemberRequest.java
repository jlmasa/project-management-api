package com.jlmasa.pms_backend.project.dto;

import com.jlmasa.pms_backend.project.domain.ProjectRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddProjectMemberRequest(@NotNull UUID organizationMembershipId, @NotNull ProjectRole projectRole) {
}
