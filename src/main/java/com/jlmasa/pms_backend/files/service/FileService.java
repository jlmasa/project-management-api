package com.jlmasa.pms_backend.files.service;

import com.jlmasa.pms_backend.files.domain.FileVersion;
import com.jlmasa.pms_backend.files.domain.PmsFile;
import com.jlmasa.pms_backend.files.repository.FileVersionRepository;
import com.jlmasa.pms_backend.files.repository.PmsFileRepository;
import com.jlmasa.pms_backend.files.storage.FileStorage;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.time.Clock;
import java.util.Set;

@Service
public class FileService {

    private static final long MAX_SIZE_BYTES = 25L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png","image/jpeg","image/gif","application/pdf",
            "text/plain", "text/csv", "application/vnd.openxmlformats-officedocument.workprocessingml.document");
    private final FileStorage fileStorage;
    private final PmsFileRepository pmsFileRepository;
    private FileVersionRepository fileVersionRepository;
    private final Clock clock;

    public FileService(FileStorage fileStorage, PmsFileRepository pmsFileRepository, FileVersionRepository fileVersionRepository, Clock clock){
        this.fileStorage = fileStorage;
        this.pmsFileRepository = pmsFileRepository;
        this.fileVersionRepository = fileVersionRepository;
        this.clock = clock;
    }

    @Transactional
    public FileVersion upload(PmsPrincipal principal, MultipartFile multipartFile, String title) throws Exception {
        if (multipartFile.getSize() > MAX_SIZE_BYTES){
            throw new IllegalArgumentException("File exceeds the " + (MAX_SIZE_BYTES/1024/1024) + "MB Limit.");
        }
        String contentType = multipartFile.getContentType();
        if(contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)){
            throw new IllegalArgumentException("Unsupported content type: "+contentType);
        }
        String safeDisplayName = sanitizeForDisplay(multipartFile.getOriginalFilename());
        String checksum;
        String storageKey;
        try(var in = multipartFile.getInputStream()){
            byte[] bytes = in.readAllBytes();
            checksum = sha256Hex(bytes);
            storageKey = fileStorage.store(new java.io.ByteArrayInputStream(bytes), bytes.length);
        }
        PmsFile pmsFile =  pmsFileRepository.save(
                new PmsFile(principal.organizationId(), title != null ? title : safeDisplayName, principal.membershipId(), clock.instant()));
        FileVersion version = fileVersionRepository.save(new FileVersion(pmsFile, 1, storageKey, safeDisplayName, principal.membershipId(), clock.instant()));
        return version;
    }

    private static String sanitizeForDisplay(String originalFileName){
        if (originalFileName == null) return "unnamed";
        String base = Path_getFileName(originalFileName);
        return base.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private static String Path_getFileName(String name){
        int lastSlash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        return lastSlash >= 0 ? name.substring(lastSlash + 1) : name;
    }

    private static String sha256Hex(byte[] bytes) throws java.security.NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
