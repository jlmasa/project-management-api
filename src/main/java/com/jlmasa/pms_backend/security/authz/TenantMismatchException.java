package com.jlmasa.pms_backend.security.authz;

public class TenantMismatchException extends RuntimeException {

    public TenantMismatchException(){
        super("Resource not found.");
    }
}
