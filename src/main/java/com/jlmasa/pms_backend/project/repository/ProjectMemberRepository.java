package com.jlmasa.pms_backend.project.repository;

import com.jlmasa.pms_backend.project.domain.ProjectMember;
import com.jlmasa.pms_backend.project.domain.ProjectMemberId;
import com.jlmasa.pms_backend.project.domain.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    @Query("""
    select pm.projectRole from ProjectMember pm
    where pm.project.id =:projectId and pm.id.organizationMembershipId =:membershipId and pm.removedAt is null
""")
    Optional<ProjectRole> findActiveRole(@Param("projectId") UUID projectId, @Param("membershipId") UUID membershipId);

    List<ProjectMember> findByProjectIdAndRemovedAtIsNull(UUID projectId);
}