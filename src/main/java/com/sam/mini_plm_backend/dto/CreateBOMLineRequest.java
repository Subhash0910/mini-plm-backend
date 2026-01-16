package com.sam.mini_plm_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * CreateBOMLineRequest DTO
 * Request body for creating a BOM line item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBOMLineRequest {
    private Long componentPartId;
    private Integer lineNumber;
    private BigDecimal quantity;
    private String unitOfMeasure;
    private String referenceDesignator;
    private String notes;
    private Integer sequenceNumber;
}