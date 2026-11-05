CREATE TABLE projects (
    id                                  UUID NOT NULL,
    organization_id                     UUID NOT NULL,
    created_by_membership_id            UUID NOT NULL,
    name                                VARCHAR(255),
    code                                VARCHAR(50),
    description                         TEXT,
    project_status                      VARCHAR(20) NOT NULL DEFAULT 'PLANNING',
    start_date                          DATE,
    due_date                            DATE,
    completed_at                        TIMESTAMPTZ,
    created_at                          TIMESTAMPTZ NOT NULL,
    updated_at                          TIMESTAMPTZ NOT NULL,
    archived_at                         TIMESTAMPTZ,

    CONSTRAINT pk_projects PRIMARY KEY (id),
    CONSTRAINT fk_projects_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_projects_created_by FOREIGN KEY (created_by_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT uq_projects_org_code UNIQUE (organization_id, code),
    CONSTRAINT ck_projects_status CHECK (project_status IN ('PLANNING', 'ACTIVE', 'ON_HOLD', 'COMPLETED', 'ARCHIVED'))
);

CREATE INDEX ix_projects_organization_id ON projects (organization_id);
CREATE INDEX ix_projects_created_by ON projects (created_by_membership_id);

CREATE TABLE project_members (
    project_id                          UUID NOT NULL,
    organization_membership_id          UUID NOT NULL,
    project_role                        VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    added_by_membership_id              UUID,
    added_at                            TIMESTAMPTZ NOT NULL,
    removed_at                          TIMESTAMPTZ,

    CONSTRAINT pk_project_members PRIMARY KEY (project_id, organization_membership_id),
    CONSTRAINT fk_project_members_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_project_members_membership FOREIGN KEY (organization_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT fk_project_members_added_by FOREIGN KEY (added_by_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT ck_project_members_role CHECK (project_role IN ('OWNER', 'MANAGER', 'MEMBER', 'VIEWER'))
);

CREATE INDEX ix_project_members_memberships_id ON project_members (organization_membership_id);

CREATE TABLE milestones (
    id                                  UUID NOT NULL,
    project_id                          UUID NOT NULL ,
    name                                VARCHAR(255) NOT NULL,
    description                         TEXT,
    milestone_status                    VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    due_at                              TIMESTAMPTZ,
    completed_at                        TIMESTAMPTZ,
    created_by_membership_id            UUID NOT NULL,
    created_at                          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_milestones PRIMARY KEY (id),
    CONSTRAINT fk_milestones_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_milestones_created_by FOREIGN KEY (created_by_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT ck_milestones_status CHECK (milestone_status IN ('OPEN', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX ix_milestones_project_id ON milestones (project_id);
CREATE INDEX ix_milestones_created_by ON milestones (created_by_membership_id);

CREATE TABLE recurring_task_rules (
    id                                  UUID NOT NULL,
    project_id                          UUID NOT NULL,
    created_by_membership_id            UUID NOT NULL,
    default_assignee_membership_id      UUID NOT NULL,
    title                               VARCHAR(255) NOT NULL,
    description                         TEXT,
    recurrence_rule                     VARCHAR(500) NOT NULL,
    default_priority                    VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    next_run_at                         TIMESTAMPTZ,
    last_generated_at                   TIMESTAMPTZ,
    is_active                           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at                          TIMESTAMPTZ NOT NULL,
    updated_at                          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_recurring_task_rule PRIMARY KEY (id),
    CONSTRAINT fk_recurring_rules_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_recurring_rules_created_by FOREIGN KEY (created_by_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT fk_recurring_rules_assignee FOREIGN KEY (default_assignee_membership_id) REFERENCES organization_memberships (id)
);

CREATE INDEX ix_recurring_rules_project_id ON recurring_task_rules (project_id);
CREATE INDEX ix_recurring_rules_created_by ON recurring_task_rules (created_by_membership_id);
CREATE INDEX ix_recurring_rules_default_assignee ON recurring_task_rules (default_assignee_membership_id);