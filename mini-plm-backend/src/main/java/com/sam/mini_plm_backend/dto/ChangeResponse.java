package com.sam.mini_plm_backend.dto;

import com.sam.mini_plm_backend.enums.ChangeStatus;
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
public class ChangeResponse {
    private Long id;
    private String changeNumber;
    private ChangeType changeType;
    private ChangePriority priority;
    private String title;
    private String description;
    private ChangeStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String assignedTo;
    private LocalDateTime dueDate;
    private LocalDateTime effectiveDate;
    private String impactAssessment;
    private List<ChangeApprovalResponse> approvals;
    private int approvalProgress;
    private long pendingApprovalCount;
    private long rejectedApprovalCount;
    private List<Long> affectedPartIds;
}
