package com.jlmasa.pms_backend.calendar.repository;

import com.jlmasa.pms_backend.calendar.domain.EventParticipant;
import com.jlmasa.pms_backend.calendar.domain.EventParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {
}
