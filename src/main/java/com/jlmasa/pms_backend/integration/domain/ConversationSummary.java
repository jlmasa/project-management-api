package com.jlmasa.pms_backend.integration.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conversation_summaries")
public class ConversationSummary {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "conversation_id", nullable = false, updatable = false)
    private UUID conversationId;

    @Column(name = "source_through_message_id", updatable = false)
    private UUID sourceThroughMessageId;

    @Column(name = "summary_text", nullable = false, updatable = false)
    private String summaryText;

    @Column(name = "model_name", updatable = false)
    private String modelName;

    @Column(name = "generated_by_membership_id", updatable = false)
    private UUID generatedByMembershipId;

    @Column(name = "generated_at", nullable = false, updatable = false)
    private Instant generatedAt;

    protected ConversationSummary(){}

    public ConversationSummary(UUID conversationId, UUID sourceThroughMessageId, String summaryText, String modelName, Instant now){
        this.conversationId = Objects.requireNonNull(conversationId, "conversationId");
        this.sourceThroughMessageId = sourceThroughMessageId;
        this.summaryText = Objects.requireNonNull(summaryText,"summaryText");
        this.modelName = modelName;
        this.generatedAt = now;
    }

    public UUID getId() {return id;}
    public UUID getConversationId() {return conversationId;}
    public UUID getSourceThroughMessageId() {return sourceThroughMessageId;}
    public String getSummaryText() {return summaryText;}
    public String getModelName() {return modelName;}
    public UUID getGeneratedByMembershipId() {return generatedByMembershipId;}
    public Instant getGeneratedAt() {return generatedAt;}

    @Override
    public boolean equals(Object o){
        if(this==o)return true;
        if(!(o instanceof ConversationSummary other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
