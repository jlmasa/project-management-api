package com.jlmasa.pms_backend.messaging.service;

import com.jlmasa.pms_backend.messaging.domain.Conversation;
import com.jlmasa.pms_backend.messaging.domain.Message;
import com.jlmasa.pms_backend.messaging.dto.MessagePageResponse;
import com.jlmasa.pms_backend.messaging.repository.ConversationRepository;
import com.jlmasa.pms_backend.messaging.repository.MessageRepository;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MessageQueryService {

    private static final int DEFAULT_LIMIT = 30;
    private static final int MAX_LIMIT = 100;
    private static final Instant FAR_FUTURE = Instant.parse("9999-12-31T23:59:59Z");
    private static final UUID MAX_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private MessagingPolicy messagingPolicy;

    public MessageQueryService(ConversationRepository conversationRepository, MessageRepository messageRepository, MessagingPolicy messagingPolicy){
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.messagingPolicy = messagingPolicy;
    }

    @Transactional(readOnly = true)
    public MessagePageResponse history(PmsPrincipal principal, UUID conversationId, Instant beforeCreatedAt, UUID beforeId, Integer limit){
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow();

        MessagingDecision decision = messagingPolicy.canRead(principal.membershipId(), conversation);
        if(!decision.allowed()){
            throw new AccessDeniedException(decision.reasonCode());
        }

        ing pageSize = (limit == null || limit <= 0) ? DEFAULT_LIMIT : Math.min(limit,MAX_LIMIT);
        Instant cursorCreatedAt = beforeCreatedAt == null ? FAR_FUTURE : beforeCreatedAt;
        UUID cursorId = beforeId == null ? MAX_UUID : beforeId;

        List<Message> rows = messageRepository.findPageBeforeCursor(
                conversationId, cursorCreatedAt, cursorId, PageRequest.of(0, pageSize +1)
        );
        List<Message> page = hasMore ? rows.subList(0, pageSize) : rows;
        List<MessageResponse> items = page.stream().map(MessageQueryService::toResponse).toList();

        Message last = page.isEmpty() ? null : page.get(page.size() -1);
        return new MessagePageResponse(items, hasMore, last == null ? null : last.getCreatedAt(), last == null ? null :last.getId() );
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> threadReplies(PmsPrincipal principal, UUID parentMessageId) {
        Message parent = messageRepository.findById(parentMessageId).orElseThrow();
        MessagingDecision decision = messagingPolicy.canRead(principal.membershipId(),
                 parent.getConversation());
        if (!decision.allowed()) {
            throw new AccessDeniedException(decision.reasonCode());
        }

        return List.of();
    }
    private static MessageResponse toResponse(Message m) {
        String body = m.isDeleted() ? null : m.getBody();
        return new MessageResponse(m.getId(), m.getConversation().getId(), "(resolve display name)", body,
                m.getMessageType().name(), m.getCreatedAt());
    }

}
