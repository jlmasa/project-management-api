package com.jlmasa.pms_backend.task.repository;

import com.jlmasa.pms_backend.task.domain.TaskChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskChecklistItemRepository extends JpaRepository<TaskChecklistItem, UUID> {
}
