package com.jlmasa.pms_backend.messaging.service;

import com.jlmasa.pms_backend.messaging.domain.Conversation;
import com.jlmasa.pms_backend.messaging.domain.ConversationParticipant;
import com.jlmasa.pms_backend.messaging.domain.ParticipantRole;
import com.jlmasa.pms_backend.messaging.policy.MessagingDecision;
import com.jlmasa.pms_backend.messaging.policy.MessagingPolicy;
import com.jlmasa.pms_backend.messaging.repository.ConversationParticipantRepository;
import com.jlmasa.pms_backend.messaging.repository.ConversationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class ConversationParticipantService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessagingPolicy messagingPolicy;
    private final Clock clock;

    public ConversationParticipantService(ConversationRepository conversationRepository,
                                          ConversationParticipantRepository participantRepository,
                                          MessagingPolicy messagingPolicy, Clock clock){
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messagingPolicy = messagingPolicy;
        this.clock = clock;
    }

    @Transactional
    public ConversationParticipant addParticipant(UUID actingMembershipId, UUID conversationId, UUID targetMembershipId){
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow();
        requireModeratorOrTaskManager(actingMembershipId, conversation);

        MessagingDecision decision = conversation.isTaskConversation()
                ? (messagingPolicy.authorizedSet(conversation.getTaskId()).contains(targetMembershipId) ? MessagingDecision.ALLOW :
                MessagingDecision.deny(MessagingDecision.NOT_TASK_AUTHORIZED)) : (messagingPolicy.sameDepartment(actingMembershipId, targetMembershipId) ? MessagingDecision.ALLOW :
                MessagingDecision.deny(MessagingDecision.CROSS_DEPARTMENT_FORBIDDEN));
        if(!decision.allowed()){
            throw new AccessDeniedException(decision.reasonCode());
        }
        return participantRepository.save(
                new ConversationParticipant(conversation, targetMembershipId, ParticipantRole.MEMBER, clock.instant()));
    }

    @Transactional
    public void removeParticipant(UUID actingMembershipId, UUID conversationId, UUID targetMembershipId){
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow();
        requireModeratorOrTaskManager(actingMembershipId, conversation);
        ConversationParticipant participant = participantRepository.findActive(conversationId, targetMembershipId).orElseThrow();
        participant.remove(clock.instant());
    }

    private void requireModeratorOrTaskManager(UUID actingMembershipId, Conversation conversation){
        var actingParticipant = participantRepository.findActive(conversation.getId(), actingMembershipId);
        boolean isModerator = actingParticipant.map(p -> p.getParticipantRole() == ParticipantRole.MODERATOR).orElse(false);
        if (!isModerator){
            throw new AccessDeniedException("Only a conversation moderator (or task manager) may manage participants.");
        }
    }
}
