package com.jlmasa.pms_backend.security.authz;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

@Service
public class TenantGuard {

    public void check(UUID resourceOrganizationId, UUID callerOrganizationId){
        if(!resourceOrganizationId.equals(callerOrganizationId)){
            throw new TenantMismatchException();
        }
    }
    public void check(Supplier<UUID> resourceOrganizationIdSupplier, UUID callerOrganizationId){
        check(resourceOrganizationIdSupplier.get(), callerOrganizationId);
    }
}
