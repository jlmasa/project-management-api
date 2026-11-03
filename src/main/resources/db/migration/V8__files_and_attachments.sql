CREATE TABLE files (
    id                          UUID NOT NULL,
    organization_id             UUID NOT NULL,
    title                       VARCHAR(255) NOT NULL,
    owner_membership_id         UUID NOT NULL,
    access_scope                VARCHAR(20) NOT NULL DEFAULT 'PROJECT',
    created_at                  TIMESTAMPTZ NOT NULL,
    updated_at                  TIMESTAMPTZ NOT NULL,
    deleted_at                  TIMESTAMPTZ,

    CONSTRAINT pk_files PRIMARY KEY (id),
    CONSTRAINT fk_files_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_files_owner FOREIGN KEY (owner_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT ck_files_access_scope CHECK ( access_scope IN ('PRIVATE','PROJECT','TASK','ORGANIZATION'))
);

CREATE INDEX ix_files_organization_id ON files (organization_id);
CREATE INDEX ix_files_owner_id ON files (owner_membership_id);

CREATE TABLE file_versions (
    id                          UUID NOT NULL,
    file_id                     UUID NOT NULL,
    version_number              INT NOT NULL,
    storage_key                 VARCHAR(500) NOT NULL,
    filename                    VARCHAR(255) NOT NULL,
    mime_type                   VARCHAR(100),
    size_bytes                  BIGINT,
    checksum                    VARCHAR(128),
    uploaded_by_membership_id   UUID NOT NULL,
    created_at                  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_file_versions PRIMARY KEY (id),
    CONSTRAINT fk_file_versions_file FOREIGN KEY (file_id) REFERENCES files (id),
    CONSTRAINT fk_file_versions_uploaded_by FOREIGN KEY (uploaded_by_membership_id) REFERENCES organization_membership (id),
    CONSTRAINT uq_file_versions_file_version UNIQUE (file_id, version_number)
);

CREATE INDEX ix_file_versions_uploaded_by ON file_versions (uploaded_by_membership_id);

CREATE TABLE project_files (
    project_id                  UUID NOT NULL,
    file_id                     UUID NOT NULL,
    folder_path                 VARCHAR(500),
    linked_by_membership_id     UUID NOT NULL,
    linked_at                   TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_project_files PRIMARY KEY (project_id, file_id),
    CONSTRAINT fk_project_files_project FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT fk_project_files_file FOREIGN KEY (file_id) REFERENCES files (id),
    CONSTRAINT fk_project_files_linked_by FOREIGN KEY (linked_by_membership_id) REFERENCES organization_membership (id),
);

CREATE INDEX ix_project_files_file_id ON project_files (file_id);

CREATE TABLE message_attachments (
    message_id                  UUID NOT NULL,
    file_version_id             UUID NOT NULL,
    attached_by_membership_id   UUID NOT NULL,
    attached_at                 TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_message_attachments PRIMARY KEY (message_id, file_version_id),
    CONSTRAINT fk_attachments_message FOREIGN KEY (message_id) REFERENCES message (id),
    CONSTRAINT fk_attachments_file_version FOREIGN KEY (file_version_id) REFERENCES file_versions (id),
    CONSTRAINT fk_attachments_attached_by FOREIGN KEY (attached_by_membership_id) REFERENCES organization_membership (id),
);

CREATE INDEX ix_attachment_file_version_id ON message_attachments (file_version_id);