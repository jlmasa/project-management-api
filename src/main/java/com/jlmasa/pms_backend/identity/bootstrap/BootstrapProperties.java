package com.jlmasa.pms_backend.identity.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pms.bootstrap")
public record BootstrapProperties(String orgName, String adminEmail, String adminPassword) {

    public boolean isComplete(){
        return orgName != null && !orgName.isBlank()
                && adminEmail != null && !adminEmail.isBlank()
                && adminPassword != null && !adminPassword.isBlank();
    }
}
