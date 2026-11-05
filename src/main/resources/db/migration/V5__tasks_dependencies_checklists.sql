CREATE TABLE tasks (
    id                          UUID NOT NULL,
    project_id                  UUID NOT NULL,
    milestone_id                UUID,
    parent_task_id              UUID,
    recurring_rule_id           UUID,
    created_by_membership_id    UUID NOT NULL,
    title                       VARCHAR(255) NOT NULL,
    description                 TEXT,
    acceptance_criteria         TEXT,
    task_status                 VARCHAR(20) NOT NULL DEFAULT 'BACKLOG',
    priority                    VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    start_at                    TIMESTAMPTZ,
    due_at                      TIMESTAMPTZ,
    completed_at                TIMESTAMPTZ,
    cancelled_at                TIMESTAMPTZ,
    estimated_minutes           INT,
    created_at                  TIMESTAMPTZ NOT NULL,
    updated_at                  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_tasks primary key (id),
    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_tasks_milestone FOREIGN KEY (milestone_id) REFERENCES milestones (id),
    CONSTRAINT fk_tasks_parent FOREIGN KEY (parent_task_id) REFERENCES tasks (id),
    CONSTRAINT fk_tasks_recurring_rule FOREIGN KEY (recurring_rule_id) REFERENCES recurring_task_rules (id),
    CONSTRAINT fk_task_created_by FOREIGN KEY (created_by_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT ck_tasks_status CHECK (task_status IN ('BACKLOG', 'TO_DO', 'IN_PROGRESS', 'IN_REVIEW', 'BLOCKED', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT ck_tasks_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);
CREATE INDEX ix_tasks_project_id ON tasks (project_id);
CREATE INDEX ix_tasks_milestone_id ON tasks (milestone_id);
CREATE INDEX ix_tasks_parent_task_id ON tasks (parent_task_id);
CREATE INDEX ix_tasks_recurring_rule_id ON tasks (recurring_rule_id);
CREATE INDEX ix_tasks_created_by ON tasks (created_by_membership_id);
CREATE INDEX ix_tasks_status ON tasks (task_status);

CREATE TABLE task_dependencies (
    task_id                     UUID NOT NULL,
    depends_on_task_id          UUID NOT NULL,
    dependency_type             VARCHAR(30) NOT NULL DEFAULT 'FINISH_TO_START',
    created_by_membership_id    UUID NOT NULL,
    created_at                  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_task_dependencies PRIMARY KEY (task_id, depends_on_task_id),
    CONSTRAINT fk_task_dependencies_task FOREIGN KEY (task_id) REFERENCES tasks (id),
    CONSTRAINT fk_task_dependencies_depends_on FOREIGN KEY (depends_on_task_id) REFERENCES tasks (id),
    CONSTRAINT fk_task_dependencies_created_by FOREIGN KEY (created_by_membership_id) REFERENCES organization_memberships (id),
    CONSTRAINT ck_task_dependencies_not_self CHECK (task_id <> depends_on_task_id)
);

CREATE INDEX ix_task_dependencies_depends_on ON task_dependencies (depends_on_task_id);

CREATE TABLE task_checklist (
    id                          UUID NOT NULL,
    task_id                     UUID NOT NULL,
    title                       VARCHAR(255) NOT NULL,
    position                    INT NOT NULL DEFAULT 0,
    created_at                  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_task_checklists PRIMARY KEY (id),
    CONSTRAINT fk_task_checklists_task FOREIGN KEY (task_id) REFERENCES tasks (id)
);

CREATE INDEX ix_task_checklists_task_id ON task_checklist (task_id);

CREATE TABLE task_checklist_items (
    id                          UUID NOT NULL,
    checklist_id                UUID NOT NULL,
    title                       VARCHAR(255) NOT NULL,
    is_completed                BOOLEAN NOT NULL DEFAULT FALSE,
    completed_by_membership_id  UUID,
    completed_at                TIMESTAMPTZ,
    position                    INT NOT NULL DEFAULT 0,

    CONSTRAINT pk_task_checklist_items PRIMARY KEY (id),
    CONSTRAINT fk_checklist_items_checklist FOREIGN KEY (checklist_id) REFERENCES task_checklist (id),
    CONSTRAINT fk_checklist_items_completed_by FOREIGN KEY (completed_by_membership_id) REFERENCES organization_memberships (id)
);

CREATE INDEX ix_checklist_items_checklist_id ON task_checklist_items (checklist_id);