package com.jlmasa.pms_backend.identity.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "role_permission")
public class RolePermission {

    @EmbeddedId
    private RolePermissionId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    protected RolePermission(){}

    public RolePermission(Role role, Permission permission){
        this.role = Objects.requireNonNull(role, "role");
        this.permission = Objects.requireNonNull(permission, "permission");
        this.id = new RolePermissionId(role.getId(),permission.getId());
    }

    public RolePermissionId getId() {return id;}
    public Role getRole() {return role;}
    public Permission getPermission() {return permission;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof RolePermission other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode(){return getClass().hashCode();}

}
