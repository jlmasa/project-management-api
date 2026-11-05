CREATE TABLE calendar_events (
    id                                  UUID NOT NULL,
    organization_id                     UUID NOT NULL,
    project_id                          UUID NOT NULL,
    task_id                             UUID,
    created_by_membership_id            UUID NOT NULL,
    title                               VARCHAR(255) NOT NULL,
    description                         TEXT,
    event_type                          VARCHAR(20) NOT NULL DEFAULT 'MEETING',
    starts_at                           TIMESTAMPTZ,
    ends_at                             TIMESTAMPTZ,
    timezone                            VARCHAR(64) NOT NULL DEFAULT 'Asia/Manila',
    meeting_url                         VARCHAR(500),
    created_at                          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_calendar_events PRIMARY KEY (id),
    CONSTRAINT fk_calendar_events_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_calendar_events_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_calendar_events_task FOREIGN KEY (task_id) REFERENCES tasks (id),
    CONSTRAINT fk_calendar_events_created_by FOREIGN KEY (created_by_membership_id) REFERENCES organization_memberships(id),
    CONSTRAINT ck_calendar_events_type CHECK (event_type IN ('MEETING','MILESTONES','DEADLINE','OTHER'))
);

CREATE INDEX ix_calendar_events_organization_id ON calendar_events (organization_id);
CREATE INDEX ix_calendar_events_project_id ON calendar_events(project_id);
CREATE INDEX ix_calendar_events_starts_at ON calendar_events (starts_at);

CREATE TABLE event_participants (
    calendar_event_id                   UUID NOT NULL,
    organization_membership_id          UUID  NOT NULL,
    response_status                     VARCHAR(10) NOT NULL DEFAULT 'INVITED',

    CONSTRAINT pk_event_participants PRIMARY KEY (calendar_event_id, organization_membership_id),
    CONSTRAINT fk_event_participants_event FOREIGN KEY (calendar_event_id) REFERENCES calendar_events (id),
    CONSTRAINT fk_event_participants_membership FOREIGN KEY (organization_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT ck_event_participants_status CHECK ( response_status IN ('INVITED','ACCEPTED', 'DECLINED','TENTATIVE'))
);

CREATE TABLE meeting_notes (
    id                                  UUID NOT NULL,
    calendar_event_id                   UUID NOT NULL,
    created_by_membership_id            UUID NOT NULL,
    content                             TEXT NOT NULL,
    ai_generated_summary                TEXT,
    created_at                          TIMESTAMPTZ NOT NULL,
    updated_at                          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_meeting_notes PRIMARY KEY (id),
    CONSTRAINT fk_meeting_notes_event FOREIGN KEY (calendar_event_id) REFERENCES calendar_events (id),
    CONSTRAINT fk_meeting_notes_created_by FOREIGN KEY (created_by_membership_id) REFERENCES organization_memberships(id)
);
CREATE INDEX ix_meeting_notes_event_id ON meeting_notes (calendar_event_id);

CREATE TABLE action_item_suggestions (
    id                                  UUID NOT NULL,
    meeting_note_id                     UUID NOT NULL,
    suggested_title                     VARCHAR(255) NOT NULL,
    suggested_description               TEXT,
    proposed_assignee_membership_id     UUID,
    proposed_due_at                     TIMESTAMPTZ,
    suggestion_status                   VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    created_task_id                     UUID,
    reviewed_by_membership_id           UUID,
    reviewed_at                         TIMESTAMPTZ,
    created_at                          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_action_item_suggestions PRIMARY KEY (id),
    CONSTRAINT fk_action_items_meeting_note FOREIGN KEY (meeting_note_id) REFERENCES meeting_notes (id),
    CONSTRAINT fk_action_items_proposed_assignee FOREIGN KEY (proposed_assignee_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT fk_action_items_created_task FOREIGN KEY (created_task_id) REFERENCES tasks (id),
    CONSTRAINT fk_action_items_reviewed_by FOREIGN KEY (reviewed_by_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT ck_action_items_status CHECK (suggestion_status IN ('PENDING','APPROVED','REJECTED','CONVERTED'))
);

CREATE INDEX ix_action_items_meeting_note_id ON action_item_suggestions (meeting_note_id);

CREATE TABLE integrations (
    id                                  UUID NOT NULL,
    organization_id                     UUID NOT NULL,
    provider                            VARCHAR(20) NOT NULL,
    display_name                        VARCHAR(255) NOT NULL,
    secret_reference                    VARCHAR(500),
    integration_status                  VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    created_by_membership_id            UUID NOT NULL,
    created_at                          TIMESTAMPTZ NOT NULL,
    updated_at                          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_integrations PRIMARY KEY (id),
    CONSTRAINT fk_integrations_organization FOREIGN KEY (organization_id)
        REFERENCES organizations (id),
    CONSTRAINT fk_integrations_created_by FOREIGN KEY  (created_by_membership_id)
        REFERENCES organization_memberships (id),
    CONSTRAINT ck_integrations_provider CHECK (
        provider IN ('GITHUB', 'GITLAB','EMAIL','CALENDAR', 'CI_CD', 'OTHER')
        ),
    CONSTRAINT ck_integrations_status CHECK (
        integration_status IN ('ACTIVE', 'DISABLED', 'ERROR')
        )
);

CREATE INDEX ix_integrations_organization_id ON integrations (organization_id);

CREATE TABLE integration_events (
    id                                      UUID NOT NULL,
    integration_id                          UUID NOT NULL,
    external_event_id                       VARCHAR(255),
    event_type                              VARCHAR(100) NOT NULL,
    project_id                              UUID,
    task_id                                 UUID,
    conversation_id                         UUID,
    payload                                 JSONB,
    received_at                             TIMESTAMPTZ NOT NULL,
    processed_at                            TIMESTAMPTZ,
    processing_status                       VARCHAR(10) NOT NULL DEFAULT 'PENDING',

    CONSTRAINT pk_integration_events PRIMARY KEY (id),
    CONSTRAINT fk_integration_events_integration FOREIGN KEY (integration_id)
                                REFERENCES integrations (id),
    CONSTRAINT fk_integration_events_project FOREIGN KEY (project_id)
                                REFERENCES projects (id),
    CONSTRAINT fk_integration_events_task FOREIGN KEY (task_id)
                                REFERENCES tasks (id),
    CONSTRAINT fk_integration_events_conversation FOREIGN KEY (conversation_id) REFERENCES conversation (id),
    CONSTRAINT uq_integration_events_external UNIQUE (integration_id, external_event_id),
    CONSTRAINT ck_integration_event_status CHECK (
        processing_status IN ('PENDING','PROCESSED','FAILED','IGNORED')
        )
);
CREATE INDEX ix_integration_events_integration_id ON integration_events (integration_id);

CREATE TABLE automation_rules (
    id UUID NOT NULL,
    organization_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    trigger_type VARCHAR(50) NOT NULL,
    trigger_config JSONB NOT NULL,
    action_config JSONB NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_by_membership_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_automation_rules PRIMARY KEY (id),
    CONSTRAINT fk_automation_rules_organization FOREIGN KEY (organization_id)
                                    REFERENCES organizations (id),
    CONSTRAINT fk_automation_rules_created_by FOREIGN KEY (created_by_membership_id) REFERENCES organization_memberships (id)
);
CREATE INDEX ix_automation_rules_organization_id ON automation_rules (organization_id);

CREATE TABLE conversation_summaries(
   id UUID NOT NULL,
   conversation_id UUID NOT NULL,
   source_through_message_id UUID,
   summary_text TEXT NOT NULL,
   model_name VARCHAR(100),
   generated_by_membership_id UUID,
   generated_at TIMESTAMPTZ NOT NULL,
   CONSTRAINT pk_conversation_summaries PRIMARY KEY (id),
   CONSTRAINT fk_summaries_conversation FOREIGN KEY (conversation_id)
       REFERENCES conversation (id),
   CONSTRAINT fk_summaries_source_message FOREIGN KEY (source_through_message_id)
       REFERENCES messages (id),
   CONSTRAINT fk_summaries_generated_by FOREIGN KEY (generated_by_membership_id)
       REFERENCES organization_memberships (id)
);
CREATE INDEX ix_summaries_conversation_id ON conversation_summaries (conversation_id)

