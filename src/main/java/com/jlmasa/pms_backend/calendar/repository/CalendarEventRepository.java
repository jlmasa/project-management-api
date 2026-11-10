package com.jlmasa.pms_backend.calendar.repository;

import com.jlmasa.pms_backend.calendar.domain.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, UUID> {
}
