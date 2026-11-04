package com.jlmasa.pms_backend.integration.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "integration_events")
public class IntegrationEvent {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "integration_id", nullable = false, updatable = false)
    private Integration integration;

    @Column(name = "external_event_id", updatable = false)
    private String externalEventId;

    @Column(name = "event_type", nullable = false,updatable = false,length = 100)
    private String eventType;

    @Column(name = "project_id", updatable = false)
    private UUID projectId;

    @Column(name = "task_id", updatable = false)
    private UUID taskId;

    @Column(name = "conversation_id", updatable = false)
    private UUID conversationId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", updatable = false)
    private String payload;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 10)
    private ProcessingStatus processingStatus;

    protected IntegrationEvent() {}

    public IntegrationEvent(Integration integration, String eventType, String payloadJson, Instant now){
        this.integration = Objects.requireNonNull(integration, "integration");
        this.eventType = Objects.requireNonNull(eventType, "eventType");
        this.payload = payloadJson;
        this.receivedAt = now;
        this.processingStatus = ProcessingStatus.PENDING;
    }

    public void markProcessed(Instant now){this.processingStatus = ProcessingStatus.PROCESSED; this.processedAt = now;}
    public void markFailed(Instant now){this.processingStatus = ProcessingStatus.FAILED; this.processedAt = now;}
    public void markIgnored(Instant now){this.processingStatus = ProcessingStatus.IGNORED; this.processedAt = now;}

    public UUID getId() {return id;}
    public Integration getIntegration() {return integration;}
    public String getExternalEventId() {return externalEventId;}
    public String getEventType() {return eventType;}
    public UUID getProjectId() {return projectId;}
    public UUID getTaskId() {return taskId;}
    public UUID getConversationId() {return conversationId;}
    public String getPayload() {return payload;}
    public Instant getReceivedAt() {return receivedAt;}
    public Instant getProcessedAt() {return processedAt;}
    public ProcessingStatus getProcessingStatus(){return processingStatus;}

    @Override
    public boolean equals(Object o){
        if (this == o)return true;
        if(!(o instanceof IntegrationEvent other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
