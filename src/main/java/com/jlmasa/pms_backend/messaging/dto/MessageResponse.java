package com.jlmasa.pms_backend.messaging.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(UUID id, UUID conversationId, String senderDisplayName, String body, String messageType, Instant createdAt) {
}
