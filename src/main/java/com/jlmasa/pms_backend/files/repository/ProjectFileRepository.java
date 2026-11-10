package com.jlmasa.pms_backend.files.repository;

import com.jlmasa.pms_backend.files.domain.ProjectFile;
import com.jlmasa.pms_backend.files.domain.ProjectFileId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, ProjectFileId> {
    List<ProjectFile> findByProjectId(UUID projectId);
}
