package com.sam.mini_plm_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BomDto {
    private Long id;
    private String name;
    private String partNumber;
    private Double quantityRequired;
    private Boolean isAssembly;
    private List<BomDto> subParts;
}
