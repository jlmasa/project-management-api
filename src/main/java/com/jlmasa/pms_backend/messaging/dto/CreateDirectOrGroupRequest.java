package com.jlmasa.pms_backend.messaging.dto;

import com.jlmasa.pms_backend.messaging.domain.ConversationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateDirectOrGroupRequest(
        @NotNull ConversationType type,
        @Size(max = 255) String name,
        UUID ProjectId,
        @NotEmpty List<UUID> participantMembershipIds
        ) {
}
