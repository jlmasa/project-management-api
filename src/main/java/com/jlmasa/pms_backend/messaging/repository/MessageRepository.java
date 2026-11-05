package com.jlmasa.pms_backend.messaging.repository;

import com.jlmasa.pms_backend.messaging.domain.Message;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("""
        select m from Message m
        where m.conversation.id =:conversationId
        and (:beforeCreatedAt is null or m.createdAt < :beforeCreatedAt)
        order by m.createdAt desc 
""")
    Slice<Message> findPageBeforeCreatedAt(@Param("conversationId") UUID conversationId,
                                           @Param("beforeCreatedAt") java.time.Instant beforeCreatedAt,
                                           org.springframework.data.domain.Pageable pageable);
}
