package com.sam.mini_plm_backend.dto;

import com.sam.mini_plm_backend.enums.Role;
import jakarta.validation.constraints.NotNull;

public class UpdateUserRoleRequest {

    @NotNull
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
