CREATE TABLE conversation (
    id                          UUID NOT NULL,
    organization_id             UUID NOT NULL,
    project_id                  UUID,
    task_id                     UUID,
    task_cycle                  INT,
    conversation_type           VARCHAR(20) NOT NULL,
    name                        VARCHAR(255),
    conversation_status         VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by_membership_id    UUID NOT NULL,
    created_at                  TIMESTAMPTZ NOT NULL ,
    closed_at                   TIMESTAMPTZ,
    closure_reason              VARCHAR(20),

    CONSTRAINT pk_conversations PRIMARY KEY (id),
    CONSTRAINT fk_conversations_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_conversations_projects FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_conversations_task FOREIGN KEY (task_id) REFERENCES tasks (id),
    CONSTRAINT fk_conversations_created_by FOREIGN KEY (created_by_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT uq_conversations_task_cycle UNIQUE (task_id, task_cycle),
    CONSTRAINT ck_conversations_type CHECK (conversation_type IN ('DIRECT', 'GROUP', 'PROJECT_CHANNEL', 'TASK')),
    CONSTRAINT ck_conversations_status CHECK (conversation_status IN ('ACTIVE', 'CLOSED','ARCHIVED')),
    CONSTRAINT ck_conversations_closure_reason CHECK (closure_reason IS NULL OR closure_reason IN ('TASK_COMPLETED', 'TASK_CANCELLED','MANUAL', 'ARCHIVED')),
    -- BR-05: TASK conversations require task_id and a positive task_cycle; every other type requires neither.
CONSTRAINT ck_conversations_task_fields CHECK (
    (conversation_type = 'TASK' AND task_id IS NOT NULL AND task_cycle IS NOT NULL AND task_cycle >0) OR (conversation_type <> 'TASK' AND task_id IS NULL AND task_cycle IS NULL)
    )
);

CREATE INDEX ix_conversations_organization_id ON conversation (organization_id);
CREATE INDEX ix_conversations_project_id ON conversation(project_id);
CREATE INDEX ix_conversations_created_by ON conversation(created_by_membership_id);

CREATE TABLE conversation_participants (
    conversation_id             UUID NOT NULL,
    organization_membership_id  UUID NOT NULL,
    participant_role            VARCHAR(10) NOT NULL DEFAULT 'MEMBER',
    joined_at                   TIMESTAMPTZ NOT NULL,
    removed_at                  TIMESTAMPTZ,
    last_read_at                TIMESTAMPTZ,
    notifications_muted         BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_conversation_participants PRIMARY KEY (conversation_id, organization_membership_id),
    CONSTRAINT fk_participants_conversation FOREIGN KEY (conversation_id) REFERENCES conversation (id),
    CONSTRAINT fk_participants_membership FOREIGN KEY (organization_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT ck_participants_role CHECK (participant_role  IN ('MEMBER','MODERATOR'))
);

CREATE INDEX ix_participants_membership_id ON conversation_participants (organization_membership_id);

CREATE TABLE messages (
    id                          UUID NOT NULL,
    conversation_id             UUID NOT NULL,
    sender_membership_id        UUID NOT NULL,
    parent_message_id          UUID,
    message_type                VARCHAR(10) NOT NULL DEFAULT 'TEXT',
    body                        TEXT,
    created_at                  TIMESTAMPTZ NOT NULL,
    edited_at                   TIMESTAMPTZ,
    deleted_at                  TIMESTAMPTZ,

    CONSTRAINT pk_messages PRIMARY KEY (id),
    CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id) REFERENCES conversation (id),
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT fk_messages_parent FOREIGN KEY (parent_message_id) REFERENCES messages (id),
    CONSTRAINT ck_messages_type CHECK (
        message_type IN ('TEXT','SYSTEM','FILE','AUDIO','VIDEO')
        )
);

CREATE INDEX ix_messages_conversation_id ON messages (conversation_id);
CREATE INDEX ix_messages_sender_id ON messages (sender_membership_id);
CREATE INDEX ix_messages_parent_id ON messages (parent_message_id);

CREATE TABLE message_reactions (
    message_id                  UUID NOT NULL,
    reacted_by_membership_id    UUID NOT NULL,
    emoji                       VARCHAR(32) NOT NULL,
    created_at                  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_message_reactions PRIMARY KEY (message_id, reacted_by_membership_id, emoji),
    CONSTRAINT fk_reactions_message FOREIGN KEY (message_id) REFERENCES messages (id),
    CONSTRAINT fk_reactions_reacted_by FOREIGN KEY (reacted_by_membership_id) REFERENCES organization_memberships (id)
);

CREATE INDEX ix_reactions_reacted_by ON message_reactions (reacted_by_membership_id);

CREATE TABLE message_mentions (
    message_id                  UUID NOT NULL,
    mentioned_membership_id     UUID NOT NULL,
    created_at                  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_message_mentions PRIMARY KEY (message_id, mentioned_membership_id),
    CONSTRAINT fk_mentioned_message FOREIGN KEY (message_id) REFERENCES messages (id),
    CONSTRAINT fk_mentions_mentioned FOREIGN KEY (mentioned_membership_id) REFERENCES organization_memberships (id)
);

CREATE INDEX ix_mentions_mentioned ON message_mentions (mentioned_membership_id);