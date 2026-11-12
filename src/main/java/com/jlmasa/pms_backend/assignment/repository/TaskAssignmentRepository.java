package com.jlmasa.pms_backend.assignment.repository;

import com.jlmasa.pms_backend.assignment.domain.TaskAssignment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, UUID> {

    Optional<TaskAssignment> findByTaskIdAndEndedAtIsNull(UUID taskId);
    List<TaskAssignment> findByTaskIdOrderByAssignedAtDesc(UUID taskId);
    List<TaskAssignment> findByAssignedToMembershipIdAndAssignedAtBetween(
            UUID membershipId, java.time.Instant from, java.time.Instant to);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ta from TaskAssignment ta where ta.id = : id")
    Optional<TaskAssignment> findByIdForUpdate(@Param("id") UUID id);

}
