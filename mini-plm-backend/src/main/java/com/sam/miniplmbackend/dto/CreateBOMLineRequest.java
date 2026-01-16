package com.sam.miniplmbackend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBOMLineRequest {
    private Long componentPartId;
    private Integer lineNumber;
    private Double quantity;
    private String unitOfMeasure;
    private String referenceDesignator;
    private String notes;
    private Integer sequenceNumber;
}