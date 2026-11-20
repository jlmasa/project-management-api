package com.jlmasa.pms_backend.files.web;

import com.jlmasa.pms_backend.files.domain.FileVersion;
import com.jlmasa.pms_backend.files.dto.UploadFileResponse;
import com.jlmasa.pms_backend.files.service.FileService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final FileService fileService;

    public FileUploadController(FileService fileService){
        this.fileService = fileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadFileResponse upload(PmsPrincipal principal, @RequestParam("file") MultipartFile file, @RequestParam(required = false) String title) throws Exception{
        FileVersion version = fileService.upload(principal, file, title);
        return new UploadFileResponse(version.getFile().getId(), version.getId(), version.getFilename(),
                file.getSize(), version.getVersionNumber(), version.getCreatedAt());
    }
}
