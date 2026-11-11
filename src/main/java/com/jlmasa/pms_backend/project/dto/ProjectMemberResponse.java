package com.jlmasa.pms_backend.project.dto;

import com.jlmasa.pms_backend.project.domain.ProjectRole;

import java.time.Instant;
import java.util.UUID;

public record ProjectMemberResponse(UUID organizationMembershipId, ProjectRole projectRole, Instant addedAt) {
}
