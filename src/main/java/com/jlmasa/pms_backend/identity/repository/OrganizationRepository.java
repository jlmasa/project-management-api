package com.jlmasa.pms_backend.identity.repository;

import com.jlmasa.pms_backend.identity.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findBySlug(String slug);
}
