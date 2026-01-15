package com.sam.mini_plm_backend.service;

import com.sam.mini_plm_backend.dto.*;
import com.sam.mini_plm_backend.entity.*;
import com.sam.mini_plm_backend.enums.*;
import com.sam.mini_plm_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangeService {

    private final ChangeRepository changeRepository;
    private final ChangeApprovalRepository changeApprovalRepository;
    private final PartRepository partRepository;
    private final ChangeHistoryRepository changeHistoryRepository;

    /**
     * Create a new change request with approval workflow (Windchill‑like)
     * Flow: DRAFT (on create) -> PENDING_APPROVAL (on submit) -> APPROVED/REJECTED -> IMPLEMENTED
     */
    public ChangeResponse createChange(CreateChangeRequest request, String userId) {
        Change change = Change.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getChangeType())
                .priority(request.getPriority())
                .createdBy(userId)
                // Start as DRAFT, like Windchill "Create Change Notice" before routing
                .status(ChangeStatus.DRAFT)
                .impactAssessment(request.getImpactAssessment())
                .assignedTo(request.getAssignedTo())
                .dueDate(request.getDueDate())
                .effectiveDate(request.getEffectiveDate())
                .build();

        if (request.getAffectedPartIds() != null && !request.getAffectedPartIds().isEmpty()) {
            List<Part> affectedParts = partRepository.findAllById(request.getAffectedPartIds());
            change.setAffectedParts(new HashSet<>(affectedParts));
        }

        Change savedChange = changeRepository.save(change);

        // History: creation as DRAFT
        logChangeHistory(savedChange, null, ChangeStatus.DRAFT, "Change request created as DRAFT", userId);

        return mapToResponse(savedChange);
    }

    /**
     * Explicitly submit a DRAFT change into the approval workflow.
     * This is the Windchill‑style "Submit for Review" step.
     */
    public ChangeResponse submitChange(Long changeId, SubmitChangeRequest request, String userId) {
        Change change = changeRepository.findById(changeId)
                .orElseThrow(() -> new RuntimeException("Change not found with ID: " + changeId));

        if (change.getStatus() != ChangeStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT changes can be submitted. Current status: " + change.getStatus());
        }

        // VALIDATION: approverIds must not be empty
        if (request.getApproverIds() == null || request.getApproverIds().isEmpty()) {
            throw new RuntimeException("At least one approver is required to submit a change");
        }

        ChangeStatus oldStatus = change.getStatus();

        // Attach approvers on submit (in order)
        for (int i = 0; i < request.getApproverIds().size(); i++) {
            String approverId = request.getApproverIds().get(i);
            if (approverId == null || approverId.trim().isEmpty()) {
                throw new RuntimeException("Approver ID at position " + (i + 1) + " cannot be empty");
            }

            ChangeApproval approval = ChangeApproval.builder()
                    .change(change)
                    .approverId(approverId.trim())
                    .approvalOrder(i + 1)
                    .status(ApprovalStatus.PENDING)
                    .build();
            changeApprovalRepository.save(approval);
        }

        change.setStatus(ChangeStatus.PENDING_APPROVAL);
        change.setLastModifiedBy(userId);
        change.setLastModifiedAt(LocalDateTime.now());

        Change updated = changeRepository.save(change);
        
        // FIX: Validate newStatus before logging
        logChangeHistory(updated, oldStatus, ChangeStatus.PENDING_APPROVAL, 
                        "Submitted for approval by: " + String.join(", ", request.getApproverIds()), userId);

        // CRITICAL: Refresh the change object to load the newly created approvals
        // This ensures mapToResponse has access to the approvals relationship
        Change refreshed = changeRepository.findById(updated.getId())
                .orElseThrow(() -> new RuntimeException("Failed to refresh change after submit"));
        
        return mapToResponse(refreshed);
    }

    /**
     * Get all changes
     */
    public List<ChangeResponse> getAllChanges() {
        return changeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get change by ID
     */
    public ChangeResponse getChangeById(Long id) {
        Change change = changeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Change not found with ID: " + id));
        return mapToResponse(change);
    }

    /**
     * Approve or reject a change (multi-level approval)
     */
    public ChangeResponse approveChange(Long changeId, ApproveChangeRequest request, String approverId) {
        Change change = changeRepository.findById(changeId)
                .orElseThrow(() -> new RuntimeException("Change not found with ID: " + changeId));

        // Normalize approver ID (trim whitespace)
        String normalizedApproverId = approverId.trim();

        ChangeApproval approval = changeApprovalRepository
                .findByChangeAndApproverIdAndStatus(change, normalizedApproverId, ApprovalStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("No pending approval found for approver: " + approverId + ". Change ID: " + changeId));

        // Validate status
        if (request.getStatus() != ApprovalStatus.APPROVED && request.getStatus() != ApprovalStatus.REJECTED) {
            throw new RuntimeException("Invalid approval status: " + request.getStatus());
        }

        approval.setStatus(request.getStatus());
        approval.setComments(request.getComments());
        approval.setApprovedAt(LocalDateTime.now());
        changeApprovalRepository.save(approval);

        ChangeStatus oldStatus = change.getStatus();

        // If rejected, mark change as rejected and stop processing
        if (request.getStatus() == ApprovalStatus.REJECTED) {
            change.setStatus(ChangeStatus.REJECTED);
            change.setLastModifiedBy(approverId);
            change.setLastModifiedAt(LocalDateTime.now());
            changeRepository.save(change);
            
            // FIX: Validate newStatus before logging
            logChangeHistory(change, oldStatus, ChangeStatus.REJECTED, 
                            "Rejected by " + approverId + ": " + request.getComments(), approverId);
            
            // Refresh to get updated approvals
            Change refreshed = changeRepository.findById(change.getId())
                    .orElseThrow(() -> new RuntimeException("Failed to refresh change"));
            return mapToResponse(refreshed);
        }

        // Check if all approvals are complete
        List<ChangeApproval> allApprovals = changeApprovalRepository.findByChange(change);
        boolean allApproved = allApprovals.stream()
                .allMatch(a -> a.getStatus() == ApprovalStatus.APPROVED);

        // If all approvals are done, mark change as APPROVED
        if (allApproved) {
            change.setStatus(ChangeStatus.APPROVED);
            change.setApprovedBy(approverId);
            change.setApprovedAt(LocalDateTime.now());
            change.setLastModifiedBy(approverId);
            change.setLastModifiedAt(LocalDateTime.now());
            changeRepository.save(change);
            
            // FIX: Validate newStatus before logging
            logChangeHistory(change, oldStatus, ChangeStatus.APPROVED, 
                            "All approvals complete. Final approval by: " + approverId, approverId);
        } else {
            // Still waiting for other approvals
            change.setLastModifiedBy(approverId);
            change.setLastModifiedAt(LocalDateTime.now());
            changeRepository.save(change);
            
            // FIX: Validate newStatus before logging
            logChangeHistory(change, oldStatus, ChangeStatus.PENDING_APPROVAL, 
                            "Approved by " + approverId + ". Awaiting further approvals.", approverId);
        }

        // Refresh to get updated approvals
        Change refreshed = changeRepository.findById(change.getId())
                .orElseThrow(() -> new RuntimeException("Failed to refresh change"));
        return mapToResponse(refreshed);
    }

    /**
     * Implement an approved change
     */
    public ChangeResponse implementChange(Long changeId, String userId) {
        Change change = changeRepository.findById(changeId)
                .orElseThrow(() -> new RuntimeException("Change not found with ID: " + changeId));

        // Only approved changes can be implemented
        if (change.getStatus() != ChangeStatus.APPROVED) {
            throw new RuntimeException("Only APPROVED changes can be implemented. Current status: " + change.getStatus());
        }

        ChangeStatus oldStatus = change.getStatus();
        change.setStatus(ChangeStatus.IMPLEMENTED);
        change.setImplementedAt(LocalDateTime.now());
        change.setLastModifiedBy(userId);
        change.setLastModifiedAt(LocalDateTime.now());

        Change updated = changeRepository.save(change);
        
        // FIX: Validate newStatus before logging
        logChangeHistory(updated, oldStatus, ChangeStatus.IMPLEMENTED, 
                        "Change implemented by: " + userId, userId);

        return mapToResponse(updated);
    }

    /**
     * Get changes by status
     */
    public List<ChangeResponse> getChangesByStatus(ChangeStatus status) {
        return changeRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get changes assigned to a user
     */
    public List<ChangeResponse> getChangesByAssignedTo(String userId) {
        return changeRepository.findByAssignedTo(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get changes created by a user
     */
    public List<ChangeResponse> getChangesByCreatedBy(String userId) {
        return changeRepository.findByCreatedBy(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get urgent changes (due within next 30 days)
     */
    public List<ChangeResponse> getUrgentChanges() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysLater = now.plusDays(30);

        return changeRepository.findByStatusAndDueDateBetween(
                        ChangeStatus.PENDING_APPROVAL, now, thirtyDaysLater)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Helper: Log change history with validation
     * Ensures newStatus is never NULL (database constraint violation prevention)
     * oldStatus, newStatus, changedBy, comments, changedAt
     */
    private void logChangeHistory(Change change, ChangeStatus oldStatus, 
                                  ChangeStatus newStatus, String comments, String userId) {
        // VALIDATION: Ensure required fields are not null
        if (change == null) {
            throw new IllegalArgumentException("Change entity cannot be null when logging history");
        }
        
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus cannot be null in change history. Old status was: " + oldStatus);
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("changedBy (userId) cannot be null or empty");
        }

        ChangeHistory history = ChangeHistory.builder()
                .change(change)
                .oldStatus(oldStatus)
                .newStatus(newStatus)  // ✅ Always non-null now
                .comments(comments)
                .changedBy(userId.trim())
                .build();
        
        changeHistoryRepository.save(history);
    }

    /**
     * Helper: Map Change entity to ChangeResponse DTO
     */
    private ChangeResponse mapToResponse(Change change) {
        // Map approvals to DTOs
        List<ChangeApprovalResponse> approvals = new java.util.ArrayList<>();
        if (change.getApprovals() != null && !change.getApprovals().isEmpty()) {
            approvals = change.getApprovals()
                    .stream()
                    .map(a -> ChangeApprovalResponse.builder()
                            .id(a.getId())
                            .approverId(a.getApproverId())
                            .approvalOrder(a.getApprovalOrder())
                            .status(a.getStatus())
                            .comments(a.getComments())
                            .approvedAt(a.getApprovedAt())
                            .build())
                    .collect(Collectors.toList());
        }

        // Calculate approval progress safely
        int approvalProgress = 0;
        if (!approvals.isEmpty()) {
            approvalProgress = (int) (approvals.stream()
                    .filter(a -> a.getStatus() == ApprovalStatus.APPROVED)
                    .count() * 100 / approvals.size());
        }

        // Get pending and rejected approval counts
        long pendingApprovalCount = approvals.stream()
                .filter(a -> a.getStatus() == ApprovalStatus.PENDING)
                .count();

        long rejectedApprovalCount = approvals.stream()
                .filter(a -> a.getStatus() == ApprovalStatus.REJECTED)
                .count();

        // Map affected parts
        List<Long> affectedPartIds = new java.util.ArrayList<>();
        if (change.getAffectedParts() != null && !change.getAffectedParts().isEmpty()) {
            affectedPartIds = change.getAffectedParts()
                    .stream()
                    .map(Part::getId)
                    .collect(Collectors.toList());
        }

        return ChangeResponse.builder()
                .id(change.getId())
                .changeNumber(change.getChangeNumber())
                .changeType(change.getType())
                .priority(change.getPriority())
                .title(change.getTitle())
                .description(change.getDescription())
                .status(change.getStatus())
                .createdBy(change.getCreatedBy())
                .createdAt(change.getCreatedAt())
                .dueDate(change.getDueDate())
                .effectiveDate(change.getEffectiveDate())
                .impactAssessment(change.getImpactAssessment())
                .assignedTo(change.getAssignedTo())
                .approvals(approvals)
                .approvalProgress(approvalProgress)
                .pendingApprovalCount(pendingApprovalCount)
                .rejectedApprovalCount(rejectedApprovalCount)
                .affectedPartIds(affectedPartIds)
                .build();
    }
}
