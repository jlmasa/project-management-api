package com.jlmasa.pms_backend.identity.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "departments")
public class Department {

    @Id @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id") private UUID id;

    @Column(name = "name") private String name;
    @Column(name = "code") private String code;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name ="organization_id", nullable = false)
    private Organization organization;

   protected Department(){

   }

   public Department(Organization organization, String name, String code, Instant now){
    this.organization = Objects.requireNonNull(organization, "organization");
    this.name = Objects.requireNonNull(name, "name");
    this.code = Objects.requireNonNull(code, "code");
    this.createdAt = now;
   }

   public UUID getId(){return id;}
    public Organization getOrganization(){return organization;}
    public String getName() {return name;}
    public String getCode() {return code;}
    public Instant getCreatedAt () {return createdAt;}

    @Override
    public boolean equals(Object o){
       if(this == o) return true;
       if(!(o instanceof Department other)) return false;
       return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
