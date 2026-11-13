package com.jlmasa.pms_backend.messaging.repository;

import com.jlmasa.pms_backend.messaging.domain.Message;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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


    @Query("""
select m from Message m
where m.conversation.id = :conversationId
and (:beforeCreatedAt is null
or m.createdAt < :beforeCreatedAt
or (m.createdAt = :beforeCreatedAt and m.id < :beforeId))
order by m.createdAt desc, m.id desc
""")
    List<Message> findPageBeforeCursor(@Param("conversationId") java.util.UUID conversationId,
                                       @Param("beforeCreatedAt") java.time.Instant beforeCreatedAt, @Param("beforeId") java.util.UUID beforeId,
                                       org.springframework.data.domain.Pageable pageable);
}
