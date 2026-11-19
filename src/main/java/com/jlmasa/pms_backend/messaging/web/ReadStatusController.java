package com.jlmasa.pms_backend.messaging.web;

import com.jlmasa.pms_backend.messaging.service.ReadStatusService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversations/{conversationId}/read-status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService){
        this.readStatusService = readStatusService;
    }

    @PostMapping
    public void markRead(PmsPrincipal principal, @PathVariable UUID conversationId){
        readStatusService.markRead(principal.membershipId(), conversationId);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(PmsPrincipal principal, @PathVariable UUID conversationId){
        return Map.of("unreadCount", readStatusService.unreadCount(principal.membershipId(), conversationId));
    }
}
