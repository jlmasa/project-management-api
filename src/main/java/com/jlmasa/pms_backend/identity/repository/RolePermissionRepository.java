package com.jlmasa.pms_backend.identity.repository;

import com.jlmasa.pms_backend.identity.domain.RolePermission;
import com.jlmasa.pms_backend.identity.domain.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
}
