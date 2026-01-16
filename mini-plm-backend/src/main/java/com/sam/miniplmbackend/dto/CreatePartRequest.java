package com.sam.miniplmbackend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartRequest {
    private String partNumber;
    private String name;
    private String description;
    private String version;
}