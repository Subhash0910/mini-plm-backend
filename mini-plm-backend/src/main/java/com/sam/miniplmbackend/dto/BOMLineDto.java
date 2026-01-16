package com.sam.miniplmbackend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMLineDto {
    private Long id;
    private String componentPartNumber;
    private String componentPartName;
    private String componentPartDescription;
    private Integer lineNumber;
    private Double quantity;
    private String unitOfMeasure;
    private String referenceDesignator;
    private String notes;
    private Integer sequenceNumber;
}