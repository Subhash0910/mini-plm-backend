package com.sam.mini_plm_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Windchill-like "Submit for Review" request.
 *
 * When a Change is in DRAFT, submitting routes it into approval workflow.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitChangeRequest {

    /**
     * Ordered list of approver user IDs/usernames.
     * The service will create ChangeApproval records in this order.
     */
    private List<String> approverIds;

    /**
     * Optional routing comment (why this change is being submitted).
     */
    private String comments;
}
