package com.jlmasa.pms_backend.messaging.repository;

import com.jlmasa.pms_backend.messaging.domain.MessageMention;
import com.jlmasa.pms_backend.messaging.domain.MessageMentionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageMentionRepository extends JpaRepository<MessageMention, MessageMentionId> {
}
