package com.sam.miniplmbackend.dto;

import lombok.*;
import java.time.LocalDateTime;
import com.sam.miniplmbackend.enums.LifecycleState;

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