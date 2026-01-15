package com.sam.mini_plm_backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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
    private LocalDateTime createdAt;
    private List<BOMLineDto> bomLines;
}
