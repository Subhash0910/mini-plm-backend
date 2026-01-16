package com.sam.mini_plm_backend.features.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * DTO for updating existing documents
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateDTO {

    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    private String documentType;
    private String state;
}
