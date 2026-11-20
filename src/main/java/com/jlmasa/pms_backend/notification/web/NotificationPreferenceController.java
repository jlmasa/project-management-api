package com.jlmasa.pms_backend.notification.web;

import com.jlmasa.pms_backend.notification.dto.UpdatePreferenceRequest;
import com.jlmasa.pms_backend.notification.service.NotificationPreferenceService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification-preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    public NotificationPreferenceController(NotificationPreferenceService preferenceService){
        this.preferenceService = preferenceService;
    }

    @PutMapping
    public void update(PmsPrincipal principal, @Valid @RequestBody UpdatePreferenceRequest request){
        preferenceService.upsert(principal.membershipId(), request.eventType(), request.deliveryChannel(), request.enabled());
    }
}
