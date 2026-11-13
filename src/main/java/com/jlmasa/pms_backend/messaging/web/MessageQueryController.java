package com.jlmasa.pms_backend.messaging.web;

import com.jlmasa.pms_backend.messaging.dto.MessagePageResponse;
import com.jlmasa.pms_backend.messaging.service.MessageQueryService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversations/{conversationId}/messages")
public class MessageQueryController {

    private final MessageQueryService messageQueryService;

    public MessageQueryController(MessageQueryService messageQueryService){
        this.messageQueryService = messageQueryService;
    }

    @GetMapping
    public MessagePageResponse history(PmsPrincipal principal, @PathVariable UUID conversationId, @RequestParam(required = false)Instant beforeCreatedAt,
                                       @RequestParam(required = false) UUID beforeId,
                                       @RequestParam(required = false) Integer limit){
        return messageQueryService.history(principal, conversationId, beforeCreatedAt, beforeId, limit);
    }
}
