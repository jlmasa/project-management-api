package com.jlmasa.pms_backend.identity.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "name", nullable = false) private String name;

    @Column(name = "description", nullable = false) private String description;

    @Column(name="is_system_role", nullable = false) private Boolean systemRole;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private Instant createdAt;

   protected Role() {}
    public Role(Organization organization, String name, String description, boolean systemRole, Instant now){
       this.organization = Objects.requireNonNull(organization, "organization");
       this.name = Objects.requireNonNull(name, "name");
       this.description = description;
       this.systemRole = systemRole;
       this.createdAt = now;
    }

    public UUID getId() {return id;}
    public Organization getOrganization() {return organization;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public boolean isSystemRole() {return systemRole;}
    public Instant getCreatedAt() {return createdAt;}

    @Override
    public boolean equals(Object o) {
       if(this ==o) return true;
       if(!(o instanceof Role other)) return false;
       return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode(){return getClass().hashCode();}
}
