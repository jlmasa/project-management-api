package com.jlmasa.pms_backend.security.authz;

import org.springframework.security.access.AccessDeniedException;

public class AccessDeniedForPermissionException extends AccessDeniedException {

    public AccessDeniedForPermissionException(String missingPermissionCode){
        super("Missing permission: "+ missingPermissionCode);
    }
}
