package com.jlmasa.pms_backend.identity.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
   @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;
    @Column(name = "slug")
    private String slug;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updatedAt", nullable = false)
    private Instant updatedAt;

    protected Organization() {
        //JPA Only
    }
    public Organization (String name, String slug, Instant now){
        this.name = Objects.requireNonNull(name, "name");
        this.slug = Objects.requireNonNullElse(slug, "slug");
        this.createdAt = now;
        this.updatedAt = now;
    }
    public void rename(String newName, Instant now){
        this.name = Objects.requireNonNull(newName, "newName");
        this.updatedAt = now;
    }
    public UUID getId() {return id;}
    public String getName(){return name;}
    public String getSlug(){return slug;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getUpdatedAt() {return updatedAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof Organization other)) return false;
        return id != null  && id.equals(other.id);
    }
    @Override
    public int hashCode(){return getClass().hashCode();}
}