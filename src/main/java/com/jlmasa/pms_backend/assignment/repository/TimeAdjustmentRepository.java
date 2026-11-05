package com.jlmasa.pms_backend.assignment.repository;

import com.jlmasa.pms_backend.assignment.domain.TimeAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TimeAdjustmentRepository extends JpaRepository<TimeAdjustment, UUID> {
    List<TimeAdjustment> findByWorkSessionIdOrderByCorrectedAtAsc(UUID workSessionId);
}
