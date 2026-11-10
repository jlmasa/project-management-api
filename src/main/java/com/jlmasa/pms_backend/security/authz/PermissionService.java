package com.jlmasa.pms_backend.security.authz;

import com.jlmasa.pms_backend.identity.repository.MembershipRoleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissionService {

    private final MembershipRoleRepository membershipRoleRepository;

    public PermissionService(MembershipRoleRepository membershipRoleRepository){
        this.membershipRoleRepository = membershipRoleRepository;
    }

    public boolean hasPermission(UUID membershipId, String permissionCode){
        return membershipRoleRepository.findPermissionCodesForMembership(membershipId).contains(permissionCode);
    }
    public void requirePermission(UUID membershipId, String permissionCode){
        if(!hasPermission(membershipId, permissionCode)){
            throw new AccessDeniedForPermissionException(permissionCode);
        }
    }
}
