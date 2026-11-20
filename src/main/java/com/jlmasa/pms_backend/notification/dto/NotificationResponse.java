package com.jlmasa.pms_backend.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, String notificationType, String title, UUID targetTaskId,
                                   UUID targetConversationId, UUID targetMessageId, Instant createdAt,
                                   Instant readAt) {
}
