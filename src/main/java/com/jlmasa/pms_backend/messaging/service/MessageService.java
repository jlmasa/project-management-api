package com.jlmasa.pms_backend.messaging.service;

import com.jlmasa.pms_backend.audit.repository.ActivityLogRepository;
import com.jlmasa.pms_backend.audit.repository.AuditLogRepository;
import com.jlmasa.pms_backend.identity.repository.OrganizationMembershipRepository;
import com.jlmasa.pms_backend.messaging.domain.Conversation;
import com.jlmasa.pms_backend.messaging.domain.Message;
import com.jlmasa.pms_backend.messaging.domain.MessageType;
import com.jlmasa.pms_backend.messaging.policy.MessagingDecision;
import com.jlmasa.pms_backend.messaging.policy.MessagingPolicy;
import com.jlmasa.pms_backend.messaging.repository.ConversationRepository;
import com.jlmasa.pms_backend.messaging.repository.MessageRepository;
import com.jlmasa.pms_backend.notification.domain.ActivityLog;
import com.jlmasa.pms_backend.notification.domain.AuditLog;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private static final int MAX_BODY_LENGTH = 1000;

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final MessagingPolicy messagingPolicy;
    private final ActivityLogRepository activityLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final Clock clock;

    public MessageService(ConversationRepository conversationRepository, MessageRepository messageRepository,
                          OrganizationMembershipRepository membershipRepository, MessagingPolicy messagingPolicy,
                          ActivityLogRepository activityLogRepository, AuditLogRepository auditLogRepository, Clock clock){
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.membershipRepository = membershipRepository;
        this.messagingPolicy = messagingPolicy;
        this.activityLogRepository = activityLogRepository;
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
    }

    @Transactional
    public Message send(PmsPrincipal principal, UUID conversationId, String body, MessageType messageType,
                        UUID parentMessageId, List<UUID> mentionedMembershipIds, List<UUID> attachmentFileVersionIds) {
        validateInput(body, messageType, attachmentFileVersionIds);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> denied(principal, "MESSAGE_SEND_DENIED", "CONVERSATION_NOT_FOUND"));
        if (!conversation.getOrganizationId().equals(principal.organizationId())) {
            throw denied(principal, "MESSAGE_SEND_DENIED", "CONVERSATION_NOT_FOUND");
        }

        MessagingDecision decision = messagingPolicy.canSend(principal.membershipId(), conversation);
        if (!decision.allowed()) {
            throw denied(principal, "MESSAGE_SEND_DENIED", decision.reasonCode());
        }
        if (parentMessageId != null) {
            Message parent = messageRepository.findById(parentMessageId).orElseThrow();
            if (!parent.getConversation().getId().equals(conversationId)) {
                throw new IllegalArgumentException("parentMessageId must be in the same conversation.");
            }
        }
        for (UUID mentioned : mentionedMembershipIds == null ? List.<UUID>of() : mentionedMembershipIds) {
            MessagingDecision mentionDecision = messagingPolicy.canMention(mentioned, conversation);
            if (!mentionDecision.allowed()) {
                throw denied(principal, "MESSAGE_SEND_DENIED", "MENTION_" + mentionDecision.reasonCode());
            }
        }
        Message message = new Message(conversation, principal.membershipId(), body, clock.instant());
        message = messageRepository.save(message);

        ActivityLog activity = new ActivityLog(null, "MESSAGE_SENT", principal.membershipId(), clock.instant());
        activity.withProjectAndTask(null, conversation.getTaskId());
        activityLogRepository.save(activity);

        UUID finalConversationId = conversationId;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
            }
        });
        return message;
    }
    private void validateInput(String body, MessageType messageType, List<UUID> attachmentFileVersionIds){
        boolean hasAttachment = attachmentFileVersionIds != null && !attachmentFileVersionIds.isEmpty();
        if(messageType == MessageType.TEXT && (body == null || body.isBlank())){
            throw new IllegalArgumentException("body is required for TEXT messages.");
        }
        if(messageType == MessageType.FILE && (body == null || body.isBlank()) && !hasAttachment){
            throw new IllegalArgumentException("A FILE message needs either a body o rat least one attachment.");
        }
        if(messageType == MessageType.SYSTEM){
            throw new IllegalArgumentException("messageType SYSTEM may not be sent by a client.");
        }
        if(body != null && body.length() > MAX_BODY_LENGTH){
            throw new IllegalArgumentException("body exceeds the maximum length of " + MAX_BODY_LENGTH + ".");
        }
    }
    private AccessDeniedException denied(PmsPrincipal principal, String action, String reasonCode){
        AuditLog event = new AuditLog(principal.organizationId(), action, "message", null, clock.instant());
        event.withActor(principal.membershipId());
        event.withDetails("{\"reason\":\"" + reasonCode + "\"}");
        auditLogRepository.save(event);
        return new AccessDeniedException(reasonCode);
    }
}
