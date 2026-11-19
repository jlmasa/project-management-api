package com.jlmasa.pms_backend.messaging.web;

import com.jlmasa.pms_backend.messaging.domain.Message;
import com.jlmasa.pms_backend.messaging.domain.MessageType;
import com.jlmasa.pms_backend.messaging.dto.MessageResponse;
import com.jlmasa.pms_backend.messaging.dto.SendMessageRequest;
import com.jlmasa.pms_backend.messaging.service.MessageService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversation/{conversationId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService){
        this.messageService = messageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse send(PmsPrincipal principal, @PathVariable UUID conversationId,
                                @Valid @RequestBody SendMessageRequest request){
        MessageType type = request.messageType() == null ? MessageType.TEXT : MessageType.valueOf(request.messageType());
        List<UUID> mentions = request.mentionedMembershipId() == null ? List.of() : request.mentionedMembershipId();
        List<UUID> attachments = request.attachmentFileVersionIds() == null ? List.of() : request.attachmentFileVersionIds();
        Message message = messageService.send(principal, conversationId, request.body(), type, request.parentMessageId(), mentions, attachments);
        return new MessageResponse(message.getId(), conversationId, "(resolve display name)", message.getBody(),
                message.getMessageType().name(), message.getCreatedAt());
    }
}
