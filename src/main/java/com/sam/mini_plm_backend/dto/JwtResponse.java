package com.sam.mini_plm_backend.dto;

import com.sam.mini_plm_backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JwtResponse DTO
 * 
 * Response payload for successful authentication.
 * Contains JWT token and user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Role role;

    /**
     * Constructor for quick response with token only
     */
    public JwtResponse(String token) {
        this.token = token;
        this.type = "Bearer";
    }
}
