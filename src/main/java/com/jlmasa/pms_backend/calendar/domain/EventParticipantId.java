package com.jlmasa.pms_backend.calendar.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EventParticipantId implements Serializable {
    private UUID calendarEventId;
    private UUID organizationMembershipId;

    protected EventParticipantId() {}

    public EventParticipantId(UUID calendarEventId, UUID organizationMembershipId){
        this.calendarEventId = Objects.requireNonNull(calendarEventId);
        this.organizationMembershipId = Objects.requireNonNull(organizationMembershipId);
    }

    public UUID getCalendarEventId() {return calendarEventId;}
    public UUID getOrganizationMembershipId() {return organizationMembershipId;}

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof EventParticipantId other)) return false;
        return calendarEventId.equals(other.calendarEventId) && organizationMembershipId.equals(other.organizationMembershipId);
    }

    @Override
    public int hashCode() {return Objects.hash(calendarEventId, organizationMembershipId);}

}
