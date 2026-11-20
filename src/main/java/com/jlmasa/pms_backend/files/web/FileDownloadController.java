package com.jlmasa.pms_backend.files.web;

import com.jlmasa.pms_backend.files.service.FileDownloadService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/versions/{fileVersionId}/download")
public class FileDownloadController {

    private final FileDownloadService fileDownloadService;

    public FileDownloadController(FileDownloadService fileDownloadService){
        this.fileDownloadService = fileDownloadService;
    }

    @GetMapping
    public ResponseEntity<org.springframework.core.io.InputStreamResource> download(
            PmsPrincipal principal, @PathVariable UUID fileVersionId) throws Exception {
        var file = fileDownloadService.download(principal, fileVersionId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.mimetype()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename() + "\"")
                .header("X-Content-Type-Options", "nosniff")
                .body(new org.springframework.core.io.InputStreamResource(file.data()));
    }

    @ExceptionHandler(FileDownloadService.FileNotFoundForCaller.class)
    public ResponseEntity<Void> handleNotFoundForCaller() {
        return ResponseEntity.notFound().build();
    }
}
