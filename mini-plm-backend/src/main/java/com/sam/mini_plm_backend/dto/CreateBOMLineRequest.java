package com.sam.mini_plm_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBOMLineRequest {

    @NotNull(message = "Component part ID is required")
    private Long componentPartId;

    @NotNull(message = "Line number is required")
    private Integer lineNumber;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double quantity;

    private String unitOfMeasure;
    private String referenceDesignator;
    private String notes;
    private Integer sequenceNumber;
}
