package com.sam.mini_plm_backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMLineDto {

    private Long id;
    private Long componentPartId;
    private String componentPartNumber;
    private String componentPartName;
    private Integer lineNumber;
    private Double quantity;
    private String unitOfMeasure;
    private String referenceDesignator;
    private String notes;
    private Integer sequenceNumber;
}
