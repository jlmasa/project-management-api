package com.jlmasa.pms_backend.notifications.repository;

import com.jlmasa.pms_backend.notifications.domain.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {
}
