package com.jlmasa.pms_backend.messaging.dto;

import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record SendMessageRequest(
        @Size(max = 10000) String body,
        String messageType,
        UUID parentMessageId,
        List<UUID> mentionedMembershipId,
        List<UUID> attachmentFileVersionIds
) {
}
