package com.jlmasa.pms_backend.identity.repository;

import com.jlmasa.pms_backend.identity.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
}
