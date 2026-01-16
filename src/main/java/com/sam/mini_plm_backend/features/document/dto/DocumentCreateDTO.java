package com.sam.mini_plm_backend.features.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for creating new documents
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateDTO {

    @NotBlank(message = "Document number is required")
    @Size(min = 2, max = 50, message = "Document number must be between 2 and 50 characters")
    private String documentNumber;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    private String documentType;
    private String revision;
}
