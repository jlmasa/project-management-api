package com.jlmasa.pms_backend.calendar.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "action_item_suggestions")
public class ActionItemSuggestion {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_note_id", nullable = false, updatable = false)
    private MeetingNote meetingNote;

    @Column(name = "suggested_title", nullable = false, updatable = false)
    private String suggestedTitle;

    @Column(name = "suggested_description", updatable = false)
    private String suggestedDescription;

    @Column(name = "proposed_assignee_membership_id", updatable = false)
    private UUID proposedAssigneeMembershipId;

    @Column(name = "proposed_due_at", updatable = false)
    private Instant proposedDueAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "suggestion_status", nullable = false, length = 10)
    private SuggestionStatus suggestionStatus;

    @Column(name = "created_task_id")
    private UUID createdTaskId;

    @Column(name = "reviewed_by_membership_id")
    private UUID reviewedByMembershipId;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ActionItemSuggestion() {}

    public ActionItemSuggestion(MeetingNote meetingNote, String suggestedTitle, Instant now){
        this.meetingNote = Objects.requireNonNull(meetingNote, "meetingNote");
        this.suggestedTitle = Objects.requireNonNull(suggestedTitle, "suggestedTitle");
        this.suggestionStatus = SuggestionStatus.PENDING;
        this.createdAt = now;
    }

    public void approveAndConvert(UUID createdTaskId, UUID reviewedByMembershipId, Instant now){
        this.suggestionStatus = SuggestionStatus.CONVERTED;
        this.createdTaskId = Objects.requireNonNull(createdTaskId, "createdTaskId");
        this.reviewedByMembershipId = Objects.requireNonNull(reviewedByMembershipId, "reviewedByMembershipId");
        this.reviewedAt = now;
    }

    public void reject(UUID reviewedByMembershipId, Instant now){
        this.suggestionStatus = SuggestionStatus.REJECTED;
        this.reviewedByMembershipId = Objects.requireNonNull(reviewedByMembershipId, "reviewedByMembershipId");
        this.reviewedAt = now;
    }

    public UUID getId(){return id;}
    public MeetingNote getMeetingNote() {return meetingNote;}
    public String getSuggestedTitle() {return suggestedTitle;}
    public String getSuggestedDescription() {return suggestedDescription;}
    public UUID getProposedAssigneeMembershipId() {return proposedAssigneeMembershipId;}
    public Instant getProposedDueAt() {return proposedDueAt;}
    public SuggestionStatus getSuggestionStatus() {return suggestionStatus;}
    public UUID getCreatedTaskId() {return createdTaskId;}
    public UUID getReviewedByMembershipId() {return reviewedByMembershipId;}
    public Instant getReviewedAt() {return reviewedAt;}
    public Instant getCreatedAt() {return createdAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof ActionItemSuggestion other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}




}
