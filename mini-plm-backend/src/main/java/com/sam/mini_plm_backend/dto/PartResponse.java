package com.sam.mini_plm_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PartResponse {

    private Long id;

    private String partNumber;
    private String name;
    private String description;
    private String version;

    private String lifecycleState;
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
