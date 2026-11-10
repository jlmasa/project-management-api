package com.jlmasa.pms_backend.identity.web;

import com.jlmasa.pms_backend.identity.dto.AssignRoleRequest;
import com.jlmasa.pms_backend.identity.dto.CreateMembershipRequest;
import com.jlmasa.pms_backend.identity.service.AccountService;
import com.jlmasa.pms_backend.identity.service.MembershipService;
import com.jlmasa.pms_backend.identity.service.RoleAssignmentService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import com.jlmasa.pms_backend.security.authz.PermissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/memberships")
public class MembershipController {

    private final AccountService accountService;
    private final MembershipService membershipService;
    private final RoleAssignmentService roleAssignmentService;
    private final PermissionService permissionService;

    public MembershipController(AccountService accountService, MembershipService membershipService, RoleAssignmentService roleAssignmentService, PermissionService permissionService){
        this.accountService = accountService;
        this.membershipService = membershipService;
        this.roleAssignmentService = roleAssignmentService;
        this.permissionService = permissionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccountAndMembership(PmsPrincipal principal, @Valid @RequestBody CreateMembershipRequest request){
        permissionService.requirePermission(principal.membershipId(), "USER_PROVISION");
        var account = accountService.createAccount(request.email(), request.displayName(), request.temporaryPassword());
        membershipService.addMember(roleAssignmentService.organizationOf(principal), account, request.departmentId() != null ? roleAssignmentService.departmentOf(request.departmentId()) : null);
    }
    @PostMapping("/roles")
    public void assignRole(PmsPrincipal principal, @Valid @RequestBody AssignRoleRequest request){
        permissionService.requirePermission(principal.membershipId(), "ROLE_ASSIGN");
        roleAssignmentService.assign(principal, request.membershipId(), request.roleId());
    }
}
