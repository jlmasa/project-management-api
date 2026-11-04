package com.jlmasa.pms_backend.calendar.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "meeting_notes")
public class MeetingNote {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calendar_event_id", nullable = false, updatable = false)
    private CalendarEvent calendarEvent;

    @Column(name = "created_by_membership_id", nullable = false, updatable = false)
    private UUID createdByMembershipId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "ai_generated_summary")
    private String aiGeneratedSummary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected MeetingNote() {}

    public MeetingNote(CalendarEvent calendarEvent, UUID createdByMembershipId, String content, Instant now){
        this.calendarEvent = Objects.requireNonNull(calendarEvent, "calendarEvent");
        this.createdByMembershipId = Objects.requireNonNull(createdByMembershipId, "createdByMembershipId");
        this.content = Objects.requireNonNull(content, "content");
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void editContent(String newContent, Instant now){this.content = Objects.requireNonNull(newContent); this.updatedAt = now;}
    public void attachAiSummary(String summary, Instant now) {this.aiGeneratedSummary = summary; this.updatedAt = now;}

    public UUID getId() {return id;}
    public CalendarEvent getCalendarEvent() {return calendarEvent;}
    public UUID getCreatedByMembershipId() {return createdByMembershipId;}
    public String getContent() {return content;}
    public String getAiGeneratedSummary() {return aiGeneratedSummary;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getUpdatedAt() {return updatedAt;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof MeetingNote other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
