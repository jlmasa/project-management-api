package com.jlmasa.pms_backend.security;

import com.jlmasa.pms_backend.audit.repository.AuditLogRepository;
import com.jlmasa.pms_backend.messaging.domain.Conversation;
import com.jlmasa.pms_backend.messaging.policy.MessagingDecision;
import com.jlmasa.pms_backend.messaging.policy.MessagingPolicy;
import com.jlmasa.pms_backend.messaging.repository.ConversationRepository;
import com.jlmasa.pms_backend.notification.domain.AuditLog;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebSocketAuthorizationInterceptor implements ChannelInterceptor {

    private static final Pattern CONVERSATION_TOPIC = Pattern.compile("^/topic/conversations/([0-9a-fA-F-]{36})$");
    private static final Pattern NOTIFICATION_QUEUE = Pattern.compile("^/queue/notifications$");

    private final ConversationRepository conversationRepository;
    private final MessagingPolicy messagingPolicy;
    private final AuditLogRepository auditLogRepository;
    private final Clock clock;

    public WebSocketAuthorizationInterceptor(ConversationRepository conversationRepository, MessagingPolicy messagingPolicy, AuditLogRepository auditLogRepository, Clock clock){
        this.conversationRepository = conversationRepository;
        this.messagingPolicy = messagingPolicy;
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(!StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
            return message;
        }
        String destination = accessor.getDestination();
        PmsPrincipal principal = extractPrincipal(accessor);

        Matcher conversationMatch = CONVERSATION_TOPIC.matcher(destination == null ? "" : destination);
        if (conversationMatch.matches()){
            UUID conversationId = UUID.fromString(conversationMatch.group(1));
            Conversation conversation = conversationRepository.findById(conversationId).orElse(null);
            MessagingDecision decision = conversation == null
                    ? MessagingDecision.deny(MessagingDecision.NOT_PARTICIPANT)
                    : messagingPolicy.canRead(principal.membershipId(), conversation);
            if(!decision.allowed()){
                audit(principal, conversationId, decision.reasonCode());
                throw new org.springframework.messaging.MessagingException(
                        "SUBSCRIBE denied: " + decision.reasonCode());
            }
            return message;
        }
        if(NOTIFICATION_QUEUE.matcher(destination == null ? "" :destination).matches()){
            return message;
        }
        throw new org.springframework.messaging.MessagingException("SUBSCRIBE denied: unrecognized destination");
    }

    private PmsPrincipal extractPrincipal(StompHeaderAccessor accessor){
        var auth = (UsernamePasswordAuthenticationToken) accessor.getUser();
        return (PmsPrincipal) auth.getPrincipal();
    }

    private void audit(PmsPrincipal principal, UUID conversationId, String reasonCode){
        AuditLog event = new AuditLog(principal.organizationId(), "SUBSCRIPTION_DENIED", "conversations", conversationId, clock.instant());
        event.withActor(principal.membershipId());
        event.withDetails("{\"reason\":\"" + reasonCode + "\"}");
        auditLogRepository.save(event);
    }
}
