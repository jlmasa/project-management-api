package com.jlmasa.pms_backend.task.repository;

import com.jlmasa.pms_backend.task.domain.Task;
import com.jlmasa.pms_backend.task.domain.TaskBoardRow;
import com.jlmasa.pms_backend.task.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    Page<Task> findByProjectIdAndTaskStatus(UUID projectId, TaskStatus taskStatus, Pageable pageable);

    List<Task> findByProjectId(UUID projectId);
    List<Task> findByParentTaskId(UUID parentTaskId);

    @Query("""
        select new com.jlmasa.pms_backend.task.domain.TaskBoardRow(t.id, t.title, t.taskStatus, t.priority, t.dueAt)
        from Task t
        where t.projectId = :projectId
        order by t.dueAt asc nulls last
""")
    List<TaskBoardRow> findBoardRows(@Param("projectId") UUID projectId);

    @Query("""
    select t from Task t   
    where t.id in (
    select td.task.id from TaskDependency td
    where td.dependsOnTask.id = :completedTaskId
    )
""")
    List<Task> findTaskBlockedBy(@Param("completedTaskId") UUID completedTaskId);
}
