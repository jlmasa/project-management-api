package com.jlmasa.pms_backend.assignment.repository;

import com.jlmasa.pms_backend.assignment.domain.WorkSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkSessionRepository extends JpaRepository<WorkSession, UUID> {

    Optional<WorkSession> findByTaskAssignmentIdAndSessionStatus(UUID taskAssignmentId, com.jlmasa.pms_backend.assignment.domain.SessionStatus status);

    List<WorkSession> findByTaskAssignmentIdOrderByStartedAtAsc(UUID taskAssignmentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ws from WorkSession ws where ws.id = :id")
    Optional<WorkSession> findByIdForUpdate(@Param("id") UUID id);
}
