package com.jlmasa.pms_backend.identity.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id @UuidGenerator(style = UuidGenerator.Style.TIME) @Column(name = "id", nullable = false) private UUID id;

    @Column(name = "code", nullable = false) private String code;

    @Column(name = "description", nullable = false) private String description;

protected Permission(){}

    public Permission(String code, String description){
    this.code = Objects.requireNonNull(code, "code");
    this.description = description;
    }

    public UUID getId(){return id;}
    public String getCode(){return code;}
    public String getDescription() {return description;}

    @Override
    public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof Permission other)) return false;
    return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
