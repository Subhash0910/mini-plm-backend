package com.sam.miniplmbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBOMRequest {
    private Long parentPartId;
    private String bomName;
    private String bomVersion;
    private String description;
    private List<CreateBOMLineRequest> bomLines;
}
