package com.jlmasa.pms_backend.messaging.dto;

import com.jlmasa.pms_backend.messaging.domain.ConversationStatus;
import com.jlmasa.pms_backend.messaging.domain.ConversationType;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(UUID id, ConversationType type, String name, ConversationStatus status, UUID taskId, Integer taskCycle, Instant createdAt) {
}
