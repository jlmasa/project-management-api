package com.jlmasa.pms_backend.messaging.service;

import com.jlmasa.pms_backend.audit.repository.AuditLogRepository;
import com.jlmasa.pms_backend.messaging.domain.Conversation;
import com.jlmasa.pms_backend.messaging.domain.ConversationParticipant;
import com.jlmasa.pms_backend.messaging.domain.ConversationType;
import com.jlmasa.pms_backend.messaging.domain.ParticipantRole;
import com.jlmasa.pms_backend.messaging.policy.MessagingDecision;
import com.jlmasa.pms_backend.messaging.policy.MessagingPolicy;
import com.jlmasa.pms_backend.messaging.repository.ConversationParticipantRepository;
import com.jlmasa.pms_backend.messaging.repository.ConversationRepository;
import com.jlmasa.pms_backend.notifications.domain.AuditLog;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import jakarta.security.auth.message.MessagePolicy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessagingPolicy messagingPolicy;
    private final AuditLogRepository auditLogRepository;
    private final Clock clock;

    public ConversationService(ConversationRepository conversationRepository,
                               ConversationParticipantRepository participantRepository, MessagingPolicy messagingPolicy,
                               AuditLogRepository auditLogRepository, Clock clock){
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messagingPolicy = messagingPolicy;
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
    }

    @Transactional
    public Conversation createDirectOrGroup(PmsPrincipal principal, ConversationType type, String name,
                                            UUID projectId, List<UUID> participantMembershipIds){
        if (type == ConversationType.DIRECT && participantMembershipIds.size() != 1){
            throw new IllegalArgumentException("DIRECT requires exactly on other participant");
        }
        List<UUID> allParticipants = java.util.stream.Stream.concat(
                java.util.stream.Stream.of(principal.membershipId()),
                participantMembershipIds.stream()).distinct().toList();
        MessagingDecision decision = messagingPolicy.canCreate(principal.membershipId(), type,allParticipants);
        if (!decision.allowed()){
            auditDenied(principal, "CONVERSATION_CREATE_DENIED", decision.reasonCode());
            throw new AccessDeniedException(decision.reasonCode());
        }
        Instant now = clock.instant();
        Conversation conversation = conversationRepository.save(
                Conversation.directOrGroupOrChannel(type, principal.organizationId(), projectId, name,principal.membershipId(), now));
        allParticipants.forEach(membershipId -> participantRepository.save(
                new ConversationParticipant(conversation, membershipId, ParticipantRole.MEMBER, now)
        ));
        return conversation;
    }

    @Transactional
    public Conversation createForTask(PmsPrincipal principal, UUID taskId){
        MessagingDecision decision = messagingPolicy.canCreateTaskConversation(principal.membershipId(), taskId);
        if (!decision.allowed()){
            auditDenied(principal, "TASK_CONVERSATION_CREATE_DENIED", decision.reasonCode());
            throw new AccessDeniedException(decision.reasonCode());
        }
        int nextCycle = conversationRepository.findHighestCycle(taskId) + 1;
        Instant now = clock.instant();
        Conversation conversation = conversationRepository.save(
                Conversation.forTask(principal.organizationId(), taskId, nextCycle, principal.membershipId(), now));
        messagingPolicy.authorizedSet(taskId).forEach(membershipId -> participantRepository.save(
                new ConversationParticipant(conversation, membershipId, ParticipantRole.MEMBER, now)
        ));
        return conversation;
    }
    private void auditDenied(PmsPrincipal principal, String action, String reasonCode){
        AuditLog event = new AuditLog(principal.organizationId(), action, "conversations", null, clock.instant());
        event.withActor(principal.membershipId());
        event.withDetails("{\"reason\":\"" + reasonCode + "\"}");
        auditLogRepository.save(event);
    }
}
