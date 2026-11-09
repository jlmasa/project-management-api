package com.jlmasa.pms_backend.identity.repository;

import com.jlmasa.pms_backend.identity.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
}