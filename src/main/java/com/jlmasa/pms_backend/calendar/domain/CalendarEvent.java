package com.jlmasa.pms_backend.calendar.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

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



}
