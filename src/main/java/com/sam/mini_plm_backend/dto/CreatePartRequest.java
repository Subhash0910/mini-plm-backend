package com.sam.mini_plm_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreatePartRequest DTO
 * Request body for creating a new part
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartRequest {
    private String partNumber;
    private String description;
    private Boolean isAssembly;
}