package com.jlmasa.pms_backend.identity.repository;

import com.jlmasa.pms_backend.notifications.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
