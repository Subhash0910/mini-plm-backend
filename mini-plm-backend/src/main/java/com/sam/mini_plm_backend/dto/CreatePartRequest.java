package com.sam.mini_plm_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePartRequest {

    @NotBlank(message = "Part number is required")
    @Size(max = 100, message = "Part number too long")
    private String partNumber;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name too long")
    private String name;

    @Size(max = 1000, message = "Description too long")
    private String description;

    @Size(max = 100, message = "Version too long")
    private String version;
}
