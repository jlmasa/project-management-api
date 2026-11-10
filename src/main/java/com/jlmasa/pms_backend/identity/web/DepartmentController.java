package com.jlmasa.pms_backend.identity.web;

import com.jlmasa.pms_backend.identity.dto.CreateDepartmentRequest;
import com.jlmasa.pms_backend.identity.dto.DepartmentResponse;
import com.jlmasa.pms_backend.identity.service.DepartmentService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import com.jlmasa.pms_backend.security.authz.PermissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final PermissionService permissionService;

    public DepartmentController(DepartmentService departmentService, PermissionService permissionService){
        this.departmentService = departmentService;
        this.permissionService = permissionService;
    }

    @GetMapping
    public List<DepartmentResponse> list(PmsPrincipal principal){
        return departmentService.listForOrganization(principal.organizationId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(PmsPrincipal principal, @Valid @RequestBody CreateDepartmentRequest request){
        permissionService.requirePermission(principal.membershipId(),"ORG_MANAGE");
        return departmentService.create(principal.organizationId(), request.name(), request.code());
    }
}
