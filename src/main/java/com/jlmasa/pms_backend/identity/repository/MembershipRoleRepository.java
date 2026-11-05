package com.jlmasa.pms_backend.identity.repository;

import com.jlmasa.pms_backend.identity.domain.MembershipRole;
import com.jlmasa.pms_backend.identity.domain.MembershipRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MembershipRoleRepository extends JpaRepository<MembershipRole, MembershipRoleId> {

    @Query("""
        select distinct p.code
        from MembershipRole mr
        join RolePermission rp on rp.role.id = mr.role.id
        join Permission p on p.id = rp.permission.id
        where mr.membership.id = :membershipId
""")
    List<String> findPermissionCodesForMembership(@Param("membershipID") UUID membershipId);
}
