package com.jlmasa.pms_backend.task.repository;

import com.jlmasa.pms_backend.task.domain.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, UUID> {
    List<TaskDependency> findByTaskId(UUID task);
}
