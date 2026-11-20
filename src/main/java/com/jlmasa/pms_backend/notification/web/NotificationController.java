package com.jlmasa.pms_backend.notification.web;

import com.jlmasa.pms_backend.common.PageResponse;
import com.jlmasa.pms_backend.notification.domain.Notification;
import com.jlmasa.pms_backend.notification.dto.NotificationResponse;
import com.jlmasa.pms_backend.notification.service.NotificationService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    @GetMapping
    public PageResponse<NotificationResponse> list(PmsPrincipal principal,
                                                   @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){
        PageResponse<Notification> result = notificationService.list(principal.membershipId(), PageRequest.of(page, size));
        return new PageResponse<>(result.content().stream().map(NotificationController::toResponse).toList(), result.page(), result.size(), result.totalElements(), result.totalPages());
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(PmsPrincipal principal){
        return Map.of("unreadCount", notificationService.unreadCount(principal.membershipId()));
    }

    @PostMapping("/{notificationId}/read")
    public void markRead(PmsPrincipal principal, @PathVariable UUID notificationId){
        notificationService.markRead(principal.membershipId(), notificationId);
    }

    @ExceptionHandler(NotificationService.NotificationNotFoundForCaller.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }

    private static NotificationResponse toResponse(Notification n){
        return new NotificationResponse(n.getId(), n.getNotificationType(), n.getTitle(), n.getTargetTaskId(),
                n.getTargetConversationId(), n.getTargetMessageId(), n.getCreatedAt(), n.getReadAt());
    }
}
