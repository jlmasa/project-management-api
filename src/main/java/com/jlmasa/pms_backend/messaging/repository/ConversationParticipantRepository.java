package com.jlmasa.pms_backend.messaging.repository;

import com.jlmasa.pms_backend.messaging.domain.ConversationParticipant;
import com.jlmasa.pms_backend.messaging.domain.ConversationParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, ConversationParticipantId> {

    @Query("""
        select cp from ConversationParticipant cp
        where cp.conversation.id =:conversationId 
        and cp.id.organizationMembershipId =:membershipId and cp.removedAt is null
""")
    Optional<ConversationParticipant> findActive(@Param("conversationId") UUID conversationId, @Param("membershipId") UUID membershipId);
}
