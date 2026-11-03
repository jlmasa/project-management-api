CREATE TABLE department(
    id                  UUID NOT NULL,
    organization_id     UUID NOT NULL,
    name                VARCHAR(255) NOT NULL,
    code                VARCHAR(50) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_departments PRIMARY KEY (id),
    CONSTRAINT fk_departments_organization FOREIGN KEY (organization_id) REFERENCES organization (id),
    CONSTRAINT uq_departments_org_code UNIQUE (organization_id, code)
);

CREATE INDEX ix_departments_organization_id ON department (organization_id);

CREATE TABLE organization_membership (
    id                  UUID NOT NULL ,
    organization_id     UUID NOT NULL ,
    user_id             UUID NOT NULL,
    department_id       UUID NOT NULL ,
    employee_code       VARCHAR(50),
    membership_status   VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    joined_at           TIMESTAMPTZ NOT NULL,
    left_at             TIMESTAMPTZ,

    CONSTRAINT pk_organization_membership PRIMARY KEY (id),
    CONSTRAINT fk_membership_organization FOREIGN KEY (organization_id) REFERENCES organization (id),
    CONSTRAINT fk_membership_user FOREIGN KEY (user_id) REFERENCES userAccount (id),
    CONSTRAINT fk_membership_department FOREIGN KEY (department_id) REFERENCES department (id),
    CONSTRAINT uq_membership_org_user UNIQUE (organization_id,user_id),
    CONSTRAINT ck_membership_status CHECK (membership_status IN ('ACTIVE', 'INVITED', 'SUSPENDED', 'LEFT'))
);

CREATE INDEX ix_membership_organization_id ON organization_membership (organization_id);
CREATE INDEX ix_membership_user_id ON organization_membership (user_id);
CREATE INDEX ix_membership_department_id ON organization_membership (department_id);