package com.sam.mini_plm_backend.dto;

import com.sam.mini_plm_backend.enums.ApprovalStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeApprovalResponse {

    private Long id;
    private String approverId;
    private Integer approvalOrder;
    private ApprovalStatus status;
    private String comments;
    private LocalDateTime approvedAt;
}
