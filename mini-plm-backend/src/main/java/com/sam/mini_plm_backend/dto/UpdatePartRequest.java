package com.sam.mini_plm_backend.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing part
 * Note: All fields are optional (can be null) for partial updates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePartRequest {

    @Size(min = 1, max = 255, message = "Part name must be between 1 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 50, message = "Version must not exceed 50 characters")
    private String version;
}
