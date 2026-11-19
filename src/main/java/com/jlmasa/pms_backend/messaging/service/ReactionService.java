package com.jlmasa.pms_backend.messaging.service;

import com.jlmasa.pms_backend.messaging.domain.Message;
import com.jlmasa.pms_backend.messaging.domain.MessageReaction;
import com.jlmasa.pms_backend.messaging.policy.MessagingDecision;
import com.jlmasa.pms_backend.messaging.policy.MessagingPolicy;
import com.jlmasa.pms_backend.messaging.repository.MessageReactionRepository;
import com.jlmasa.pms_backend.messaging.repository.MessageRepository;
import jakarta.security.auth.message.MessagePolicy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class ReactionService {

    private final MessageRepository messageRepository;
    private final MessageReactionRepository reactionRepository;
    private final MessagingPolicy messagingPolicy;
    private final Clock clock;

    public ReactionService(MessageRepository messageRepository, MessageReactionRepository reactionRepository, MessagingPolicy messagingPolicy, Clock clock){
        this.messageRepository = messageRepository;
        this.reactionRepository = reactionRepository;
        this.messagingPolicy = messagingPolicy;
        this.clock = clock;
    }

    @Transactional
    public void react(UUID actingMembershipId, UUID messageId, String emoji){
        Message message = messageRepository.findById(messageId).orElseThrow();
        MessagingDecision decision = messagingPolicy.canSend(actingMembershipId, message.getConversation());
        if(!decision.allowed()){
            throw new AccessDeniedException(decision.reasonCode());
        }
        try{
            reactionRepository.save(new MessageReaction(message, actingMembershipId, emoji, clock.instant()));
        } catch (DataIntegrityViolationException ex){

        }
    }

    @Transactional
    public void unreact(UUID actingMembershipId, UUID messageId, String emoji){
        reactionRepository.deleteById(new com.jlmasa.pms_backend.messaging.domain.MessageReactionId(messageId, actingMembershipId, emoji));
    }
}
