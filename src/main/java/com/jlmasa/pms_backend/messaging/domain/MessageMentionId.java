package com.jlmasa.pms_backend.messaging.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class MessageMentionId implements Serializable {

    private UUID messageId;
    private UUID mentionedMembershipId;

    protected MessageMentionId(){}

    public MessageMentionId(UUID messageId, UUID mentionedMembershipId){
        this.messageId = Objects.requireNonNull(messageId);
        this.mentionedMembershipId = Objects.requireNonNull(mentionedMembershipId);
    }

    public UUID getMessageId() {return messageId;}
    public UUID getMentionedMembershipId() {return mentionedMembershipId;}

    @Override
    public boolean equals(Object o){
        if (this ==o) return true;
        if(!(o instanceof MessageMentionId other)) return false;
        return messageId.equals(other.messageId) && mentionedMembershipId.equals(other.mentionedMembershipId);
    }
    @Override
    public int hashCode(){return Objects.hash(messageId, mentionedMembershipId);}
}
