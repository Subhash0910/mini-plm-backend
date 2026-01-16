package com.sam.mini_plm_backend.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * User Role Enumeration
 * 
 * Defines available roles in the application:
 * - ADMIN: Full system access
 * - MANAGER: Department/team management access
 * - USER: Standard user access
 */
public enum Role implements GrantedAuthority {
    ADMIN("ROLE_ADMIN", "Administrator - Full system access"),
    MANAGER("ROLE_MANAGER", "Manager - Department management access"),
    USER("ROLE_USER", "User - Standard user access");

    private final String authority;
    private final String description;

    Role(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }
}
