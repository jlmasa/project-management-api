package com.jlmasa.pms_backend.messaging.service;

import com.jlmasa.pms_backend.messaging.domain.ConversationParticipant;
import com.jlmasa.pms_backend.messaging.repository.ConversationParticipantRepository;
import com.jlmasa.pms_backend.messaging.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class ReadStatusService {

    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final Clock clock;

    public ReadStatusService(ConversationParticipantRepository participantRepository,
                             MessageRepository messageRepository, Clock clock){
        this.participantRepository = participantRepository;
        this.messageRepository = messageRepository;
        this.clock = clock;
    }

    @Transactional
    public void markRead(UUID membershipId, UUID conversationId){
        ConversationParticipant participant = participantRepository.findActive(conversationId, membershipId).orElseThrow();
        participant.markReadUpTo(clock.instant());
    }

    @Transactional(readOnly = true)
    public long unreadCount(UUID membershipId, UUID conversationId){
        ConversationParticipant participant = participantRepository.findActive(conversationId, membershipId).orElseThrow();
        var lastReadAt = participant.getLastReadAt();

        return messageRepository.countByConversationIdAndCreatedAtAfter(conversationId, lastReadAt == null ? java.time.Instant.EPOCH : lastReadAt);

    }
}
