package com.jlmasa.pms_backend.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePreferenceRequest(@NotBlank String eventType, @NotBlank String deliveryChannel, @NotNull Boolean enabled) {
}
