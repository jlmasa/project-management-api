package com.jlmasa.pms_backend.assignment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTimeAdjustmentRequest(
        @NotNull UUID workSessionId,
        @NotNull Integer adjustmentSeconds,
        @Size(min =  10, max = 500) String reason
        ) {
}
