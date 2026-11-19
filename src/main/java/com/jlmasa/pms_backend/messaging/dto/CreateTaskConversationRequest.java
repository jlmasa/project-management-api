package com.jlmasa.pms_backend.messaging.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTaskConversationRequest(@NotNull UUID taskId) {
}
