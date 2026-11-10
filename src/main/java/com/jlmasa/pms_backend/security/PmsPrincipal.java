package com.jlmasa.pms_backend.security;

import java.util.UUID;

public record PmsPrincipal(UUID userId, UUID organizationId, UUID membershipId ) {
}
