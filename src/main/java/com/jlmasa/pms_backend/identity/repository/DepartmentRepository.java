package com.jlmasa.pms_backend.identity.repository;

import com.jlmasa.pms_backend.identity.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    List<Department> findByOrganizationIdOrderByNameAsc(UUID organizationId);
    boolean existsByOrganizationIdAndCode(UUID organizationId, String code);
}