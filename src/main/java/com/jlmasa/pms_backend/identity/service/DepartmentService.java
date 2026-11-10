package com.jlmasa.pms_backend.identity.service;

import com.jlmasa.pms_backend.identity.domain.Department;
import com.jlmasa.pms_backend.identity.domain.Organization;
import com.jlmasa.pms_backend.identity.dto.DepartmentResponse;
import com.jlmasa.pms_backend.identity.repository.DepartmentRepository;
import com.jlmasa.pms_backend.identity.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final OrganizationRepository organizationRepository;
    private final Clock clock;

    public DepartmentService(DepartmentRepository departmentRepository, OrganizationRepository organizationRepository, Clock clock){
        this.departmentRepository = departmentRepository;
        this.organizationRepository = organizationRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> listForOrganization(UUID organizationId){
        return departmentRepository.findByOrganizationIdOrderByNameAsc(organizationId).stream().map(DepartmentService::toResponse).toList();
    }

    @Transactional
    public DepartmentResponse create(UUID organizationId, String name, String code){
        String normalizedName = name.trim();
        String normalizedCode = code.trim().toUpperCase(Locale.ROOT);

        if (departmentRepository.existsByOrganizationIdAndCode(organizationId, normalizedCode)){
            throw new IllegalStateException("A department with this code already exists in this organization");
        }
        Organization organization = organizationRepository.findById(organizationId).orElseThrow();
        Department department = departmentRepository.save(
                new Department(organization, normalizedName, normalizedCode, clock.instant()));
        return toResponse(department);
    }

    private static DepartmentResponse toResponse(Department department){
        return new DepartmentResponse(department.getId(), department.getName(), department.getCode(),department.getCreatedAt());
    }

}
