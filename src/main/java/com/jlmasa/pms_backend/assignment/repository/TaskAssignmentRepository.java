package com.jlmasa.pms_backend.assignment.repository;

import com.jlmasa.pms_backend.assignment.domain.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, UUID> {

    Optional<TaskAssignment> findByTaskIdAndEndedAtIsNull(UUID taskId);
    List<TaskAssignment> findByTaskIdOrderByAssignedAtDesc(UUID taskId);
    List<TaskAssignment> findByAssignedToMembershipIdAndAssignedAtBetween(
            UUID membershipId, java.time.Instant from, java.time.Instant to);

}
