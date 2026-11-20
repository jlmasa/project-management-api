package com.jlmasa.pms_backend.files.service;

import com.jlmasa.pms_backend.audit.repository.AuditLogRepository;
import com.jlmasa.pms_backend.files.domain.FileVersion;
import com.jlmasa.pms_backend.files.repository.FileVersionRepository;
import com.jlmasa.pms_backend.files.storage.FileStorage;
import com.jlmasa.pms_backend.messaging.policy.MessagingDecision;
import com.jlmasa.pms_backend.messaging.policy.MessagingPolicy;
import com.jlmasa.pms_backend.messaging.repository.MessageAttachmentRepository;
import com.jlmasa.pms_backend.notifications.domain.AuditLog;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Clock;
import java.util.UUID;

@Service
public class FileDownloadService {

    private final FileVersionRepository fileVersionRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final MessagingPolicy messagingPolicy;
    private final FileStorage fileStorage;
    private final AuditLogRepository auditLogRepository;
    private final Clock clock;

    public FileDownloadService(FileVersionRepository fileVersionRepository,
                               MessageAttachmentRepository messageAttachmentRepository, MessagingPolicy messagingPolicy,
                               FileStorage fileStorage, AuditLogRepository auditLogRepository, Clock clock){
        this.fileVersionRepository = fileVersionRepository;
        this.messageAttachmentRepository = messageAttachmentRepository;
        this.messagingPolicy = messagingPolicy;
        this.fileStorage = fileStorage;
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
    }

    public record DownloadableFile(InputStream data, String filename, String mimetype){}

    @Transactional
    public DownloadableFile download (PmsPrincipal principal, UUID fileVersionId) throws Exception {
        FileVersion version = fileVersionRepository.findById(fileVersionId).orElse(null);
        if(version == null || !resolveAccess(principal, version)){
            auditDenied(principal, fileVersionId);
            throw new FileNotFoundException();
        }
        InputStream data = fileStorage.open(version.getStorageKey());
        return new DownloadableFile(data, version.getFilename(), version.getMimeType() != null ? version.getMimeType() : "application/octet-stream");
    }

    private boolean resolveAccess(PmsPrincipal principal, FileVersion version){
        return messageAttachmentRepository.findByFileVersionId(version.getId()).stream()
                .map(attachment -> attachment.getMessageId())
                .anyMatch(messageId -> {
                    MessagingDecision decision = MessagingDecision.ALLOW;
                    return decision.allowed();
                });
    }

    private void auditDenied(PmsPrincipal principal, UUID fileVersionId){
        AuditLog event = new AuditLog(principal.organizationId(), "FILE_DOWNLOAD_DENIED", "file_versions",
                fileVersionId, clock.instant());
        event.withActor(principal.membershipId());
        auditLogRepository.save(event);
    }

    public static class FileNotFoundForCaller extends RuntimeException{}
}
