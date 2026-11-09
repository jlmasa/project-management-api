package com.jlmasa.pms_backend.identity.dto;

import java.util.UUID;

public record MembershipSummary(UUID membershipID, UUID organizationId, String organizationName, String displayName) {
}
