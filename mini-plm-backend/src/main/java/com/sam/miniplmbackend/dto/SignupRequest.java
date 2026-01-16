package com.sam.miniplmbackend.dto;

import lombok.*;
import jakarta.validation.constraints.Email;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String username;
    
    @Email
    private String email;
    
    private String password;
}