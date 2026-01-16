package com.sam.mini_plm_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

/**
 * BOMResponse - Data Transfer Object for BOM API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMResponse {
    private Long id;
    private Long parentPartId;
    private String parentPartNumber;
    private String bomName;
    private String bomVersion;
    private String description;
    private Boolean isActive;
    private String createdBy;
    private Date createdAt;
    private List<BOMLineDto> bomLines;
}