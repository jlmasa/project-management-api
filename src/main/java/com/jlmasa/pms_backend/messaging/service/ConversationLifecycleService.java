package com.jlmasa.pms_backend.messaging.service;

import com.jlmasa.pms_backend.audit.repository.ActivityLogRepository;
import com.jlmasa.pms_backend.messaging.domain.ClosureReason;
import com.jlmasa.pms_backend.messaging.domain.Conversation;
import com.jlmasa.pms_backend.messaging.domain.ConversationStatus;
import com.jlmasa.pms_backend.messaging.domain.Message;
import com.jlmasa.pms_backend.messaging.repository.ConversationRepository;
import com.jlmasa.pms_backend.messaging.repository.MessageRepository;
import com.jlmasa.pms_backend.notification.domain.ActivityLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversationLifecycleService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ActivityLogRepository activityLogRepository;
    private final Clock clock;

    public ConversationLifecycleService(ConversationRepository conversationRepository,
                                        MessageRepository messageRepository, ActivityLogRepository activityLogRepository, Clock clock) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.activityLogRepository = activityLogRepository;
        this.clock = clock;
    }

    @Transactional
    public void closeForTask(UUID taskId, ClosureReason reason, UUID actingMembershipId) {
        int highestCycle = conversationRepository.findHighestCycle(taskId);
        if (highestCycle == 0) {
            return;
        }
        Optional<Conversation> current = conversationRepository.findByTaskIdAndTaskCycle(taskId, highestCycle);
        if (current.isEmpty() || current.get().getConversationStatus() != ConversationStatus.ACTIVE) {
            return;
        }
        Conversation conversation = current.get();
        Instant now = clock.instant();

        Message closureNotice = new Message(conversation, actingMembershipId, "Conversation closed.", now);

        messageRepository.save(closureNotice);
        conversation.close(reason, now);

        ActivityLog activity = new ActivityLog(null, "CONVERSATION_CLOSED", actingMembershipId, now);
        activity.withProjectAndTask(null, taskId);
        activityLogRepository.save(activity);
    }

    @Transactional
    public Conversation startNextCycle(UUID taskId, UUID organizationId, UUID actingMembershipId,
                                       java.util.Set<UUID> authorizedMembershipIds) {
        int nextCycle = conversationRepository.findHighestCycle(taskId) + 1;
        Conversation conversation = conversationRepository.save(
                Conversation.forTask(organizationId, taskId, nextCycle, actingMembershipId, clock.instant()));
        authorizedMembershipIds.forEach(membershipId -> {
        });
        ActivityLog activity = new ActivityLog(null, "TASK_REOPENED_NEW_CYCLE", actingMembershipId,
                clock.instant());
        activity.withProjectAndTask(null, taskId);
        activity.withValues(null, "{\"newCycle\":" + nextCycle + "}", null);
        activityLogRepository.save(activity);
        return conversation;

    }
}