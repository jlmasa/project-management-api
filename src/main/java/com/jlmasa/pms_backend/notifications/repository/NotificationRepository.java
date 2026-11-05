package com.jlmasa.pms_backend.notifications.repository;

import com.jlmasa.pms_backend.notifications.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    long countByRecipientMembershipIdAndReadAtIsNull(UUID recipientMembershipId);

    Page<Notification> findByRecipientMembershipIdOrderByCreatedAtDesc(UUID recipientMembershipId, Pageable pageable);

    @Modifying
    @Query("update Notification n set n.readAt =:now where n.id =:id and n.recipientMembershipId = :recipientMembershipId")
    int markRead(@Param("id") UUID id, @Param("recipientMembershipId") UUID recipientMembershipId, @Param("now")Instant now);
}
