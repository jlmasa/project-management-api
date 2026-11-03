package com.jlmasa.pms_backend.messaging.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ConversationParticipantId implements Serializable {

    private UUID conversationId;
    private UUID organizationMembershipId;

    protected ConversationParticipantId() {}

    public ConversationParticipantId(UUID conversationId, UUID organizationMembershipId){
        this.conversationId = Objects.requireNonNull(conversationId);
        this.organizationMembershipId = Objects.requireNonNull(organizationMembershipId);
    }
    public UUID getConversationId() {return conversationId;}
    public UUID getOrganizationMembershipId() {return organizationMembershipId;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof ConversationParticipantId other)) return false;
        return conversationId.equals(other.conversationId) && organizationMembershipId.equals(other.organizationMembershipId);
    }
    @Override
    public int hashCode() {return Objects.hash(conversationId, organizationMembershipId);}
}
