package com.sam.mini_plm_backend.dto;

import com.sam.mini_plm_backend.enums.LifecycleState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LifecycleTransitionResponse {
    private Long id;
    private String partNumber;
    private String name;
    private LifecycleState lifecycleState;
    private Integer revisionNumber;
    private String revisionSequence;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
    private LocalDateTime releasedDate;
    private LocalDateTime obsoleteDate;
}
