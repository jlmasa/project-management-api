package com.jlmasa.pms_backend.integration.repository;

import com.jlmasa.pms_backend.integration.domain.IntegrationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IntegrationEventRepository extends JpaRepository<IntegrationEvent, UUID> {

    Optional<IntegrationEvent> findByIntegrationIdAndExternalEventId(UUID integrationId, String externalEventId);
}
