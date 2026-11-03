package com.jlmasa.pms_backend.files.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "file_versions")
public class FileVersion {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false, updatable = false)
    private PmsFile file;

    @Column(name = "version_number", nullable = false, updatable = false)
    private int versionNumber;

    @Column(name = "storage_key", nullable = false, updatable = false)
    private String storageKey;

    @Column(name = "filename", nullable = false, updatable = false)
    private String filename;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "size_bytes")
    private long sizeBytes;

    @Column(name = "checksum")
    private String checksum;

    @Column(name = "uploaded_by_membership_id", nullable = false, updatable = false)
    private UUID uploadedByMembershipId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected FileVersion(){}

    public FileVersion(PmsFile file, int versionNumber, String storageKey, String filename, UUID uploadedByMembershipId, Instant now){
        this.file = Objects.requireNonNull(file, "file");
        this.versionNumber = versionNumber;
        this.storageKey = Objects.requireNonNull(storageKey, "storageKey");
        this.filename = Objects.requireNonNull(filename, "filename");
        this.uploadedByMembershipId = Objects.requireNonNull(uploadedByMembershipId, "uploadedByMembershipId");
        this.createdAt = now;
    }

    public UUID getId() {return id;}
    public PmsFile getFile() {return file;}
    public int getVersionNumber() {return versionNumber;}
    public String getStorageKey() {return storageKey;}
    public String getFilename() {return filename;}
    public String getMimeType() {return mimeType;}
    public Long getSizeBytes() {return sizeBytes;}
    public UUID getUploadedByMembershipId(){return uploadedByMembershipId;}
    public Instant getCreatedAt() {return createdAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof FileVersion other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
