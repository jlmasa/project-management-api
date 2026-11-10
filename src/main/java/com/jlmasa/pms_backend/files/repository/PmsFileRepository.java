package com.jlmasa.pms_backend.files.repository;

import com.jlmasa.pms_backend.files.domain.PmsFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PmsFileRepository extends JpaRepository<PmsFile, UUID> {
}
