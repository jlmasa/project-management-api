package com.jlmasa.pms_backend.messaging.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class MessageReactionId implements Serializable {

    private UUID messageId;
    private UUID reactedByMembershipId;
    private String emoji;

    protected MessageReactionId(){}

    public MessageReactionId(UUID messageId, UUID reactedByMembershipId, String emoji){
        this.messageId = Objects.requireNonNull(messageId);
        this.reactedByMembershipId = Objects.requireNonNull(reactedByMembershipId);
        this.emoji = Objects.requireNonNull(emoji);
    }

    public UUID getMessageId() {return messageId;}
    public UUID getReactedByMembershipId() {return reactedByMembershipId;}
    public String getEmoji() {return emoji;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof MessageReactionId other)) return false;
        return messageId.equals(other.messageId) && reactedByMembershipId.equals(other.reactedByMembershipId) && emoji.equals(other.emoji);
    }

    @Override
    public int hashCode() {return Objects.hash(messageId, reactedByMembershipId, emoji);}
}
