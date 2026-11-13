package com.jlmasa.pms_backend.messaging.dto;

import java.time.Instant;
import java.util.UUID;

public record MessagePageRequest(Instant beforeCreatedAt, UUID beforeId, Integer limit) {
}
