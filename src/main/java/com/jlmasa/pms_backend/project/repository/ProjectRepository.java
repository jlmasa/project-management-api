package com.jlmasa.pms_backend.project.repository;

import com.jlmasa.pms_backend.project.domain.Project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("""
        select p from Project p
        where p.organizationId = :organizationId
        and exists (
        select 1 from ProjectMember pm
        where pm.project.id = p.id and pm.id.organizationMembershipId = :membershipId and pm.removedAt is null
        )
        """)
    Page<Project> findVisibleToMembership(@Param("organizationId") UUID organizationId,
                                          @Param("membershipId") UUID membershipId, Pageable pageable);
}
