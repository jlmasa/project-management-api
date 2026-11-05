package com.jlmasa.pms_backend.messaging.repository;

import com.jlmasa.pms_backend.messaging.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Optional<Conversation> findByTaskIdAndTaskCycle(UUID taskId, Integer taskCycle);

    @Query("select coalesce(max(c.taskCycle), 0) from Conversation c where c.taskId =:taskId")
    int findHighestCycle(@Param("taskId") UUID taskId);
}
