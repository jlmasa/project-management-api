CREATE TABLE task_assignments (
    id                              UUID NOT NULL ,
    task_id                         UUID NOT NULL,
    assigned_to_membership_id       UUID NOT NULL,
    assigned_by_membership_id       UUID NOT NULL,
    department_id_at_assignment     UUID,
    assignment_status               VARCHAR(20) NOT NULL DEFAULT 'ASSIGNED',
    assigned_at                     TIMESTAMPTZ NOT NULL,
    first_started_at                TIMESTAMPTZ,
    completed_at                    TIMESTAMPTZ,
    ended_at                        TIMESTAMPTZ,
    end_reason                      VARCHAR(20),
    created_at                      TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_task_assignments PRIMARY KEY (id),
    CONSTRAINT fk_assignments_task FOREIGN KEY (task_id) REFERENCES task (id),
    CONSTRAINT fk_assignments_assigned_to FOREIGN KEY (assigned_by_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT fk_assignments_assigned_by FOREIGN KEY (assigned_by_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT fk_assignments_department FOREIGN KEY (department_id_at_assignment) REFERENCES department (id),
    CONSTRAINT ck_assignments_status CHECK (assignment_status IN ('ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELED', 'REASSIGNED')),
    CONSTRAINT ck_assignments_end_reason CHECK (end_reason IS NULL FOR end_reason IN ('COMPLETED', 'CANCELLED', 'REASSIGNED', 'TASK_CLOSED'))
);

CREATE INDEX ix_assignments_task_id ON task_assignments (task_id);
CREATE INDEX ix_assignments_assigned_to ON task_assignments (assigned_by_membership_id);
CREATE INDEX ix_assignments_assigned_by ON task_assignments (assigned_by_membership_id);
CREATE INDEX ix_assignments_departments ON task_assignments (department_id_at_assignment);

CREATE TABLE work_sessions (
    id                              UUID NOT NULL,
    task_assignment_id              UUID NOT NULL,
    started_by_membership_id        UUID NOT NULL,
    ended_by_membership_id          UUID,
    started_at                      TIMESTAMPTZ NOT NULL,
    ended_at                        TIMESTAMPTZ,
    duration_seconds                BIGINT,
    session_status                  VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    end_reason                      VARCHAR(20),
    created_at                      TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_work_sessions PRIMARY KEY (id),
    CONSTRAINT fk_work_sessions_assignment FOREIGN KEY (task_assignment_id) REFERENCES task_assignments(id),
    CONSTRAINT fk_work_sessions_started_by FOREIGN KEY (started_by_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT fk_work_sessions_ended_by FOREIGN KEY (ended_by_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT ck_work_sessions_status CHECK ( session_status IN ('ACTIVE', 'ENDED')),
    CONSTRAINT cK_work_sessions_end_reason CHECK (end_reason IS NULL OR end_reason IN ('PAUSE','USER_STOPPED', 'COMPLETED', 'CANCELLED', 'REASSIGNED', 'AUTO_STOPPED'))
);

CREATE INDEX ix_work_sessions_assignment_id ON work_sessions (task_assignment_id);
CREATE INDEX ix_work_sessions_started_by ON work_sessions (started_by_membership_id);
CREATE INDEX ix_work_sessions_ended_by ON work_sessions (ended_by_membership_id);

CREATE UNIQUE INDEX uq_work_sessions_one_active_per_assignment ON work_sessions (task_assignment_id) WHERE session_status = 'ACTIVE';

CREATE TABLE time_adjustments (
    id                              UUID NOT NULL,
    work_session_id                 UUID NOT NULL,
    adjustment_seconds              INT NOT NULL,
    duration_before_seconds         BIGINT NOT NULL,
    duration_after_seconds          BIGINT NOT NULL,
    reason                          TEXT NOT NULL,
    corrected_by_membership_id      UUID NOT NULL,
    corrected_at                    TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_time_adjustments PRIMARY KEY (id),
    CONSTRAINT fk_time_adjustments_session FOREIGN KEY (work_session_id) REFERENCES work_sessions (id),
    CONSTRAINT fk_time_adjustments_corrected_by FOREIGN KEY (corrected_by_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT ck_time_adjustments_math CHECK (duration_after_seconds = duration_before_seconds + adjustment_seconds)
);
CREATE INDEX ix_time_adjustments_session_id ON time_adjustments (work_session_id);