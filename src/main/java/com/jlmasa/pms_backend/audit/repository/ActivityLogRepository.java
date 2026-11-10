package com.jlmasa.pms_backend.audit.repository;

import com.jlmasa.pms_backend.notifications.domain.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
}
