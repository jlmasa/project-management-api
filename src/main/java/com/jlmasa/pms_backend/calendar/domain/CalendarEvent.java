package com.jlmasa.pms_backend.calendar.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "calendar_events")
public class CalendarEvent {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "task_id")
    private UUID taskId;

    @Column(name = "created_by_membership_id", nullable = false, updatable = false)
    private UUID createdByMembershipId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "descriptions")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private CalendarEventType eventType;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone;

    @Column(name = "meeting_url")
    private String meetingUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CalendarEvent(){}

    public CalendarEvent(UUID organizationId, UUID createdByMembershipId, String title, Instant startsAt, Instant now){
       this.organizationId = Objects.requireNonNull(organizationId, "organizationId");
       this.createdByMembershipId = Objects.requireNonNull(createdByMembershipId, "createdByMembershipId");
       this.title = Objects.requireNonNull(title, "title");
       this.eventType = CalendarEventType.MEETING;
       this.startsAt = Objects.requireNonNull(startsAt, "startsAt");
       this.timezone = "Asia/Manila";
       this.createdAt = now;
    }

    public void reschedule(Instant startsAt, Instant endAt) {this.startsAt = startsAt; this.endsAt = endsAt;}
    public void linkToProjectAndTask(UUID projectId, UUID taskId) {this.projectId = projectId; this.taskId = taskId;}

    public UUID getId() {return id;}
    public UUID getOrganizationId() {return organizationId;}
    public UUID getProjectId() {return projectId;}
    public UUID getTaskId() {return taskId;}
    public UUID getCreatedByMembershipId() {return createdByMembershipId;}
    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public CalendarEventType getEventType() {return eventType;}
    public Instant getStartsAt() {return startsAt;}
    public Instant getEndsAt() {return endsAt;}
    public String getTimezone() {return timezone;}
    public String getMeetingUrl() {return meetingUrl;}
    public Instant getCreatedAt() {return createdAt;}

    @Override
    public boolean equals(Object o){
        if (this == o ) return true;
        if(!(o instanceof CalendarEvent other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
