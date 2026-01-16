package com.sam.miniplmbackend.dto;

import lombok.*;
import java.time.LocalDateTime;
import com.sam.miniplmbackend.enums.LifecycleState;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartResponse {
    private Long id;
    private String partNumber;
    private String name;
    private String description;
    private String version;
    private LifecycleState lifecycleState;
    private Integer revisionNumber;
    private String revisionSequence;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private LocalDateTime releasedDate;
    private LocalDateTime obsoleteDate;
    private String createdBy;
    private String lastModifiedBy;
    private Boolean isAssembly;
}