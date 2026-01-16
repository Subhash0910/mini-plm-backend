package com.sam.miniplmbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartRequest {
    private String partNumber;
    private String name;
    private String description;
    private String version;
    private Boolean isAssembly;
}
