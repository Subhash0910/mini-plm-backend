package com.sam.mini_plm_backend.dto;

import com.sam.mini_plm_backend.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveChangeRequest {

    @NotNull(message = "Approval status is required")
    private ApprovalStatus status;  // APPROVED or REJECTED

    private String comments;  // Why approved/rejected
}
