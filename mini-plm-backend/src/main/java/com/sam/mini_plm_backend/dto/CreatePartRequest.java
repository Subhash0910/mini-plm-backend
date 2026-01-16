package com.sam.mini_plm_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartRequest {

    @NotBlank
    private String partNumber;

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String version;

    @NotNull
    private Boolean isAssembly;
}
