package com.jlmasa.pms_backend.files.dto;

import java.time.Instant;
import java.util.UUID;

public record UploadFileResponse(UUID fileId, UUID fileVersionId, String filename, long sizeBytes, int versionNumber, Instant createdAt) {
}
