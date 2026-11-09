package com.jlmasa.pms_backend.identity.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresInSeconds, MembershipSummary membership) {
}
