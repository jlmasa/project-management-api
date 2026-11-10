package com.jlmasa.pms_backend.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhoAmIController {

    @GetMapping("/api/v1/_whoami")
    public PmsPrincipal whoAmI(@AuthenticationPrincipal  PmsPrincipal principal) {
        return principal;
    }
}
