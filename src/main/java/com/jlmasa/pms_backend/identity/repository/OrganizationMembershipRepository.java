package com.jlmasa.pms_backend.identity.repository;

import com.jlmasa.pms_backend.identity.domain.OrganizationMembership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembershipRepository, UUID> {

    @Query("""
        select m from OrganizationMembership m
        where m.user.id = :userId and m.organization.id = :organizationId and m.membershipStatus = 'ACTIVE'
""")
    Optional<OrganizationMembership> findActiveByUserAndOrganization(
            @Param("userID") UUID userId, @Param("organizationId") UUID organizationId
    );

    Page<OrganizationMembership> findByOrganizationId(UUID organizationId, Pageable pageable);

    Optional<OrganizationMembership> findByIdAndOrganization(UUID id, UUID organizationId);
}

