package com.jlmasa.pms_backend.calendar.domain;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "event_participants")
public class EventParticipant {

    @EmbeddedId
    private EventParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("calendarEventId")
    @JoinColumn(name = "calendar_event_id", nullable = false, updatable = false)
    private CalendarEvent calendarEvent;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_status", nullable = false, length = 10)
    private EventResponseStatus responseStatus;

    protected EventParticipant() {}

    public EventParticipant(CalendarEvent calendarEvent, UUID organizationMembershipId){
        this.calendarEvent = Objects.requireNonNull(calendarEvent, "calenderEvent");
        this.id = new EventParticipantId(calendarEvent.getId(), organizationMembershipId);
        this.responseStatus = EventResponseStatus.INVITED;
    }
    public void response(EventResponseStatus status){this.responseStatus = Objects.requireNonNull(status);}

    public EventParticipantId getId() {return id;}
    public CalendarEvent getCalendarEvent() {return calendarEvent;}
    public EventResponseStatus getResponseStatus() {return responseStatus;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof EventParticipant other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
