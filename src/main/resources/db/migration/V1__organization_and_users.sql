CREATE TABLE organizations (
    id                  UUID NOT NULL,
    name                VARCHAR(255) NOT NULL,
    slug                VARCHAR(255) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_organizations PRIMARY KEY (id),
    CONSTRAINT uq_organizations_slug UNIQUE (slug)
);

CREATE TABLE userAccount (
    id                  UUID NOT NULL ,
    email               VARCHAR(255) NOT NULL ,
    password_hash       VARCHAR(255) NOT NULL ,
    display_name        VARCHAR(255) NOT NULL,
    avatar_url          VARCHAR(500),
    is_platform_admin   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,
    disabled_at         TIMESTAMPTZ,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);