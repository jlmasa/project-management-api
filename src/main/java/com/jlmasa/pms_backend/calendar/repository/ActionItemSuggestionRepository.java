package com.jlmasa.pms_backend.calendar.repository;

import com.jlmasa.pms_backend.calendar.domain.ActionItemSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActionItemSuggestionRepository extends JpaRepository<ActionItemSuggestion, UUID> {
}
