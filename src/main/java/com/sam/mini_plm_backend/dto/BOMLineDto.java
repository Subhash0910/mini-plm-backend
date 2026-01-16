package com.sam.mini_plm_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * BOMLineDto - Data Transfer Object for BOM Line responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMLineDto {
    private Long id;
    private Long componentPartId;
    private String componentPartNumber;
    private Integer lineNumber;
    private BigDecimal quantity;
    private String unitOfMeasure;
    private String referenceDesignator;
    private String notes;
    private Integer sequenceNumber;
}