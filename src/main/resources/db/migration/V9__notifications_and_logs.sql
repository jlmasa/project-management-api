CREATE TABLE notifications (
    id                          UUID NOT NULL,
    organization_id             UUID NOT NULL,
    recipient_membership_id     UUID NOT NULL,
    actor_membership_id         UUID,
    notification_type           VARCHAR(50) NOT NULL,
    title                       VARCHAR(255) NOT NULL,
    body                        TEXT,
    target_project_id           UUID,
    target_task_id              UUID,
    target_conversation_id      UUID,
    target_message_id           UUID,
    created_at                  TIMESTAMPTZ NOT NULL,
    read_at                     TIMESTAMPTZ,
    dismissed_at                TIMESTAMPTZ,

    CONSTRAINT pk_notification PRIMARY KEY (id),
    CONSTRAINT fk_notification_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT fk_notification_actor FOREIGN KEY (actor_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT fk_notification_project FOREIGN KEY (target_project_id) REFERENCES projects (id),
    CONSTRAINT fk_notification_task FOREIGN KEY (target_task_id) REFERENCES task (id),
    CONSTRAINT fk_notification_conversation FOREIGN KEY (target_conversation_id) REFERENCES conversations (id),
    CONSTRAINT fk_notification_message FOREIGN KEY (target_message_id) REFERENCES message (id),
);

CREATE INDEX ix_notifications_recipient_id ON notifications (recipient_membership_id);
CREATE INDEX ix_notifications_recipient_unread ON notifications (recipient_membership_id) WHERE read_at IS NULL;

CREATE TABLE notification_preferences (
    id                          UUID NOT NULL,
    organization_membership_id  UUID NOT NULL,
    event_type                  VARCHAR(50) NOT NULL,
    delivery_channel            VARCHAR(10) NOT NULL,
    is_enabled                  BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at                  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_notification_preferences PRIMARY KEY (id),
    CONSTRAINT fk_preferences_membership FOREIGN KEY (organization_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT uq_preferences_membership_event_channel UNIQUE (organization_membership_id, event_type, delivery_channel),
    CONSTRAINT ck_preferences_channel CHECK (delivery_channel IN ('IN_APP','EMAIL','PUSH'))
);

CREATE TABLE activity_logs (
    id                          UUID NOT NULL,
    organization_id             UUID NOT NULL,
    project_id                  UUID,
    task_id                     UUID,
    task_assignment_id          UUID,
    work_session_id             UUID,
    actor_membership_id         UUID,
    target_membership_id        UUID,
    event_type                  VARCHAR(50) NOT NULL,
    old_values                  JSONB,
    new_values                  JSONB,
    details                     JSONB,
    happened_at                 TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_activity_logs PRIMARY KEY (id),
    CONSTRAINT fk_activity_logs_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_activity_logs_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_activity_logs_task FOREIGN KEY (task_id) REFERENCES task (id),
    CONSTRAINT fk_activity_logs_assignment FOREIGN KEY (task_assignment_id) REFERENCES task_assignments (id),
    CONSTRAINT fk_activity_logs_work_session FOREIGN KEY (work_session_id) REFERENCES work_sessions (id),
    CONSTRAINT fk_activity_logs_actor FOREIGN KEY (actor_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT fk_activity_logs_target FOREIGN KEY (target_membership_id) REFERENCES organization_membership (id),
);

CREATE INDEX ix_activity_logs_organization_id ON activity_logs (organization_id);
CREATE INDEX ix_activity_logs_project_id ON activity_logs (project_id);
CREATE INDEX ix_activity_logs_task_id ON activity_logs (task_id);
CREATE INDEX ix_activity_logs_happened_at ON activity_logs (happened_at);

CREATE TABLE audit_logs (
    id                          UUID NOT NULL,
    organization_id             UUID,
    actor_membership_id         UUID,
    target_membership_id        UUID,
    action                      VARCHAR(100) NOT NULL,
    entity_type                 VARCHAR(100) NOT NULL,
    entity_id                   UUID,
    ip_address                  VARCHAR(64),
    user_agents                 TEXT,
    details                     JSONB,
    happened_at                 TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_audit_logs PRIMARY KEY (id),
    CONSTRAINT fk_audit_logs_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_audit_logs_actor FOREIGN KEY (actor_membership_id) REFERENCES  organization_membership (id),
    CONSTRAINT fk_audit_logs_target FOREIGN KEY (target_membership_id) REFERENCES organization_membership (id),
);

CREATE INDEX ix_audit_logs_organization_id ON audit_logs (organization_id);
CREATE INDEX ix_audit_logs_happened_at ON audit_logs (happened_at);