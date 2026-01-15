package com.sam.mini_plm_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBOMRequest {

    @NotNull(message = "Parent part ID is required")
    private Long parentPartId;

    @NotBlank(message = "BOM name is required")
    private String bomName;

    @NotBlank(message = "BOM version is required")
    private String bomVersion;

    private String description;

    @NotEmpty(message = "BOM must have at least one line item")
    private List<CreateBOMLineRequest> bomLines;
}
