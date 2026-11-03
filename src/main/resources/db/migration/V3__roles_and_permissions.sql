CREATE TABLE role (
    id                              UUID NOT NULL ,
    organization_id                 UUID NOT NULL ,
    name                            VARCHAR(255) NOT NULL,
    description                     TEXT,
    is_system_role                  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at                      TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_role PRIMARY KEY (id),
    CONSTRAINT fk_roles_organization FOREIGN KEY (organization_id) REFERENCES organization (id),
    CONSTRAINT uq_roles_org_name UNIQUE (organization_id, name)
);
CREATE INDEX ix_roles_organization_id ON role (organization_id);

CREATE TABLE permissions (
    id                              UUID NOT NULL,
        code                        VARCHAR(255) NOT NULL,
    description                     TEXT,

    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uq_permissions_code UNIQUE (code)
);

CREATE TABLE membership_roles (
    organization_membership_id      UUID NOT NULL,
    role_id                         UUID NOT NULL,
    assigned_by_membership_id       UUID,
    assigned_at                     TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_membership_roles PRIMARY KEY (organization_membership_id, role_id),
    CONSTRAINT fk_membership_roles_membership FOREIGN KEY (organization_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT fk_membership_roles_assigned_by FOREIGN KEY (assigned_by_membership_id) REFERENCES organization_membership (id)
);

CREATE INDEX ix_membership_roles_role_id ON membership_roles (role_id);
CREATE INDEX ix_membership_roles_assigned_by ON membership_roles(assigned_by_membership_id);

CREATE TABLE role_permissions (
    role_id                         UUID NOT NULL,
    permission_id                   UUID NOT NULL,

    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
    constraint fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES role (id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id)
);

CREATE INDEX ix_role_permissions_permission_id ON role_permissions (permission_id);