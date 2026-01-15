package com.sam.mini_plm_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new part
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePartRequest {

    @NotBlank(message = "Part number is required")
    @Size(min = 3, max = 50, message = "Part number must be between 3 and 50 characters")
    private String partNumber;

    @NotBlank(message = "Part name is required")
    @Size(min = 1, max = 255, message = "Part name must be between 1 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 50, message = "Version must not exceed 50 characters")
    private String version;
}
