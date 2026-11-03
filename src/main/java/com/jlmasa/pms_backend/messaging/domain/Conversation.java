package com.jlmasa.pms_backend.messaging.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conversation")
public class Conversation {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "task_id", updatable = false)
    private UUID taskId;

    @Column(name = "task_cycle", updatable = false)
    private Integer taskCycle;

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type", nullable = false, length = 20, updatable = false)
    private ConversationType conversationType;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_status", nullable = false, length = 20)
    private ConversationStatus conversationStatus;

    @Column(name = "created_by_membership_id", nullable = false, updatable = false)
    private UUID createdByMembershipId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "closure_reason", length = 20)
    private ClosureReason closureReason;

    protected Conversation () {}

    private Conversation(UUID organizationId, UUID projectId, UUID taskId, Integer taskCycle, ConversationType type, UUID createdByMembershipId, Instant now){
        boolean isTask = type == conversationType.TASK;
        if(isTask && (taskId == null || taskCycle == null || taskCycle <= 0)){
            throw new IllegalArgumentException("A TASK conversation requires taskId and a positive taskCycle");
        }
        if(!isTask && (taskId != null || taskCycle != null)){
            throw new IllegalArgumentException("Only TASK conversations may carry TaskId/taskCycle");
        }
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId");
        this.projectId = projectId;
        this.taskId = taskId;
        this.taskCycle = taskCycle;
        this.conversationType = type;
        this.conversationStatus = ConversationStatus.ACTIVE;
        this.createdByMembershipId = Objects.requireNonNull(createdByMembershipId, "createdByMembershipId");
        this.createdAt = now;
    }

    public static Conversation directOrGroupOrChannel(ConversationType type, UUID organizationId, UUID projectId, String name, UUID createdByMembershipId, Instant now){
        Conversation c = new Conversation(organizationId, projectId, null, null, type, createdByMembershipId, now);
        c.name = name;
        return c;
    }
    public static Conversation forTask(UUID organizationId, UUID taskId, int taskCycle, UUID createdByMembershipId, Instant now){
        return new Conversation(organizationId, null, taskId, taskCycle, ConversationType.TASK, createdByMembershipId, now);
    }

    public void close(ClosureReason reason, Instant now){
        this.conversationStatus = ConversationStatus.CLOSED;
        this.closureReason = Objects.requireNonNull(reason, "reason");
        this.closedAt = now;

    }
    public boolean isActive() {return conversationStatus == ConversationStatus.ACTIVE;}
    public boolean isTaskConversation() {return conversationType == ConversationType.TASK;}

    public UUID getId() {return id;}
    public UUID getOrganizationId() {return organizationId;}
    public UUID getProjectId() {return projectId;}
    public UUID getTaskId() {return taskId;}
    public Integer getTaskCycle() {return taskCycle;}
    public ConversationType getConversationType() {return conversationType;}
    public String getName() {return name;}
    public ConversationStatus getConversationStatus() {return conversationStatus;}
    public UUID getCreatedByMembershipId() {return createdByMembershipId;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getClosedAt() {return closedAt;}
    public ClosureReason getClosureReason() {return closureReason;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof Conversation other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
