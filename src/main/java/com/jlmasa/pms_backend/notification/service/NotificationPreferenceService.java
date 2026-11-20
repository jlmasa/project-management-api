package com.jlmasa.pms_backend.notification.service;

import com.jlmasa.pms_backend.notification.domain.DeliveryChannel;
import com.jlmasa.pms_backend.notification.domain.NotificationPreference;
import com.jlmasa.pms_backend.notification.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Set;
import java.util.UUID;

@Service
public class NotificationPreferenceService {

    private static final Set<String> NON_MUTABLE_EVENT_TYPES = Set.of("ROLE_CHANGED", "MEMBERSHIP_SUSPEND");

    private final NotificationPreferenceRepository preferenceRepository;
    private final Clock clock;

    public NotificationPreferenceService(NotificationPreferenceRepository preferenceRepository, Clock clock){
        this.preferenceRepository = preferenceRepository;
        this.clock = clock;
    }

    public boolean isEnabled(UUID membershipId, String eventType, String channel){
        if(NON_MUTABLE_EVENT_TYPES.contains(eventType)){
            return true;
        }
        return preferenceRepository.findByOrganizationMembershipIdAndEventTypeAndDeliveryChannel(
                membershipId, eventType, DeliveryChannel.valueOf(channel))
                .map(NotificationPreference::isEnabled)
                .orElse(true);
    }

    @Transactional
    public void upsert(UUID membershipId, String eventType, String channel, boolean enabled){
        if (NON_MUTABLE_EVENT_TYPES.contains(eventType)){
            throw new IllegalArgumentException("This event type cannot be muted.");
        }
        DeliveryChannel deliveryChannel = DeliveryChannel.valueOf(channel);
        preferenceRepository.findByOrganizationMembershipIdAndEventTypeAndDeliveryChannel(membershipId,
                eventType, deliveryChannel).ifPresentOrElse(
                        existing -> existing.toggle(enabled, clock.instant()),
                () -> preferenceRepository.save(
                        new NotificationPreference(membershipId, eventType, deliveryChannel, enabled, clock.instant())));
    }
}
