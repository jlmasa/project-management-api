package com.jlmasa.pms_backend.identity.dto;

import java.time.Instant;
import java.util.UUID;

public record DepartmentResponse(UUID id, String name, String code, Instant createdAt) {
}
