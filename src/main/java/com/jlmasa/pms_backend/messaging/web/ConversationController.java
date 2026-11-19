package com.jlmasa.pms_backend.messaging.web;

import com.jlmasa.pms_backend.messaging.domain.Conversation;
import com.jlmasa.pms_backend.messaging.dto.ConversationResponse;
import com.jlmasa.pms_backend.messaging.dto.CreateDirectOrGroupRequest;
import com.jlmasa.pms_backend.messaging.dto.CreateTaskConversationRequest;
import com.jlmasa.pms_backend.messaging.service.ConversationService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversations" )
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService){
        this.conversationService = conversationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse create(PmsPrincipal principal, @Valid @RequestBody CreateDirectOrGroupRequest request){
        return toResponse(conversationService.createDirectOrGroup(principal, request.type(), request.name(), request.ProjectId(), request.participantMembershipIds()));
    }

    @PostMapping("/task")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse createForTask(PmsPrincipal principal, @Valid @RequestBody CreateTaskConversationRequest request){
        return toResponse(conversationService.createForTask(principal, request.taskId()));
    }

    private static ConversationResponse toResponse(Conversation c){
        return new ConversationResponse(c.getId(), c.getConversationType(), c.getName(), c.getConversationStatus(), c.getTaskId(), c.getTaskCycle(), c.getCreatedAt());
    }


}
