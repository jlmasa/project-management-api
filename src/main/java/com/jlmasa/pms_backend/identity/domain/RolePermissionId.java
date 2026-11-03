package com.jlmasa.pms_backend.identity.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RolePermissionId implements Serializable {

    private UUID roleId;
    private UUID permissionId;

    protected RolePermissionId(){
    }

    public RolePermissionId(UUID roleId, UUID permissionId){
        this.roleId = Objects.requireNonNull(roleId);
        this.permissionId = Objects.requireNonNull(permissionId);
    }

    public UUID getRoleId() {return roleId;}
    public UUID getPermissionId() {return permissionId;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof RolePermissionId other)) return false;
        return roleId.equals(other.roleId) && permissionId.equals(other.permissionId);
    }

    @Override
    public int hashCode() {return Objects.hash(roleId, permissionId);}
}
