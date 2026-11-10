package com.jlmasa.pms_backend.files.repository;

import com.jlmasa.pms_backend.files.domain.FileVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileVersionRepository extends JpaRepository<FileVersion, UUID> {
}
