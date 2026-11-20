package com.jlmasa.pms_backend.notification.service;

import com.jlmasa.pms_backend.common.PageResponse;
import com.jlmasa.pms_backend.notification.domain.Notification;
import com.jlmasa.pms_backend.notification.repository.NotificationRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceService preferenceService;
    private final Clock clock;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationPreferenceService preferenceService,
                               Clock clock){
        this.notificationRepository = notificationRepository;
        this.preferenceService = preferenceService;
        this.clock = clock;
    }

    @Transactional
    public void createForRecipient(UUID organizationId, UUID recipientMembershipId, UUID actorMembershipId,
                                   String notificationType, String title, UUID targetTaskId,
                                   UUID targetConversationId, UUID targetMessageId){
        if(!preferenceService.isEnabled(recipientMembershipId, notificationType, "IN_APP")){
            return;
        }
        Notification notification = new Notification(organizationId, recipientMembershipId, actorMembershipId,notificationType, title, clock.instant());
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public PageResponse<Notification> list(UUID recipientMembershipId, Pageable pageable){
        return PageResponse.from(
                notificationRepository.findByRecipientMembershipIdOrderByCreatedAtDesc(recipientMembershipId, pageable),
                java.util.function.Function.identity());
    }

    @Transactional
    public void markRead(UUID recipientMembershipId, UUID notificationId){
        int updated = notificationRepository.markRead(notificationId, recipientMembershipId,clock.instant());
        if(updated == 0){
            throw new NotificationNotFoundForCaller();
        }
    }

    @Transactional(readOnly = true)
    public long unreadCount(UUID recipientMembershipId){
        return notificationRepository.countByRecipientMembershipIdAndReadAtIsNull(recipientMembershipId);
    }
    public static class NotificationNotFoundForCaller extends RuntimeException {

    }
}
