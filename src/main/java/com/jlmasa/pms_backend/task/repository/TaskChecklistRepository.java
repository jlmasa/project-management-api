package com.jlmasa.pms_backend.task.repository;

import com.jlmasa.pms_backend.task.domain.TaskChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskChecklistRepository extends JpaRepository<TaskChecklist, UUID>  {
}
