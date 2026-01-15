package com.sam.mini_plm_backend.dto;

import com.sam.mini_plm_backend.enums.ChangeType;
import com.sam.mini_plm_backend.enums.ChangePriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChangeRequest {
    private String title;
    private String description;
    private ChangeType changeType;
    private ChangePriority priority;
    private String assignedTo;
    private LocalDateTime dueDate;
    private LocalDateTime effectiveDate;
    private String impactAssessment;
    private List<Long> affectedPartIds;
    private List<String> approverIds;
}
