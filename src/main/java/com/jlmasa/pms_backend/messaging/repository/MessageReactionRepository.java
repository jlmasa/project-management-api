package com.jlmasa.pms_backend.messaging.repository;

import com.jlmasa.pms_backend.messaging.domain.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, UUID> {
}
