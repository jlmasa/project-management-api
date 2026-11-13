package com.jlmasa.pms_backend.messaging.dto;

import java.util.List;

public record MessagePageResponse(List<MessageResponse> items, boolean hasMore,
                                  java.time.Instant nextBeforeCreatedAt, java.util.UUID nextBeforeId) {
}

