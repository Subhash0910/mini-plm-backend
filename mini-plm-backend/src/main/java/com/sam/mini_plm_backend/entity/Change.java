package com.sam.mini_plm_backend.entity;

import com.sam.mini_plm_backend.enums.ChangeStatus;
import com.sam.mini_plm_backend.enums.ChangeType;
import com.sam.mini_plm_backend.enums.ChangePriority;
import com.sam.mini_plm_backend.enums.ApprovalStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "changes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Change {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // Business Identity
    // ===============================

    @Column(name = "change_number", unique = true, nullable = false)
    private String changeNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType type;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ===============================
    // Status & Priority
    // ===============================

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ChangeStatus status = ChangeStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private ChangePriority priority;

    // ===============================
    // Relationships - Affected Parts
    // ===============================

    // If you later need extra fields on the relationship, replace this with a join-entity.
    @ManyToMany
    @JoinTable(
            name = "change_parts",
            joinColumns = @JoinColumn(name = "change_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    @Builder.Default
    private Set<Part> affectedParts = new HashSet<>();

    // ===============================
    // Impact Assessment
    // ===============================

    @Column(name = "impact_assessment", columnDefinition = "TEXT")
    private String impactAssessment;

    // ===============================
    // Assignment & Deadlines
    // ===============================

    @Column(name = "assigned_to")
    private String assignedTo;  // User ID / username responsible for implementation

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;  // When change goes live

    // ===============================
    // Audit & Timestamps
    // ===============================

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "implemented_at")
    private LocalDateTime implementedAt;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    // ===============================
    // Relationships - Approvals & History
    // ===============================

    @OneToMany(mappedBy = "change", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChangeApproval> approvals = new ArrayList<>();

    @OneToMany(mappedBy = "change", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChangeHistory> history = new ArrayList<>();

    // ===============================
    // JPA Lifecycle Hooks
    // ===============================

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.lastModifiedAt = now;

        if (this.type == null) {
            // Better: throw exception, but defaulting avoids NPE during early dev.
            this.type = ChangeType.ECR;
        }
        if (this.changeNumber == null || this.changeNumber.isBlank()) {
            this.changeNumber = this.type.name() + "-" + System.currentTimeMillis();
        }
        if (this.status == null) {
            this.status = ChangeStatus.DRAFT;
        }
        if (this.affectedParts == null) {
            this.affectedParts = new HashSet<>();
        }
        if (this.approvals == null) {
            this.approvals = new ArrayList<>();
        }
        if (this.history == null) {
            this.history = new ArrayList<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }

    // ===============================
    // Business Logic Methods
    // ===============================

    /**
     * Check if change is in editable state.
     * Typically allow editing when DRAFT or PENDING_APPROVAL.
     */
    public boolean isEditable() {
        return this.status == ChangeStatus.DRAFT
                || this.status == ChangeStatus.PENDING_APPROVAL;
    }

    /**
     * Check if change can be approved.
     */
    public boolean canBeApproved() {
        return this.status == ChangeStatus.PENDING_APPROVAL;
    }

    /**
     * Check if change can be implemented.
     * Only APPROVED changes should be implemented.
     */
    public boolean canBeImplemented() {
        return this.status == ChangeStatus.APPROVED;
    }

    /**
     * Check if all approvals are complete (all APPROVED).
     */
    public boolean isFullyApproved() {
        if (this.approvals == null || this.approvals.isEmpty()) {
            return false;
        }
        return this.approvals.stream()
                .allMatch(approval -> approval.getStatus() == ApprovalStatus.APPROVED);
    }

    /**
     * Get approval progress percentage (0–100).
     */
    public Integer getApprovalProgress() {
        if (this.approvals == null || this.approvals.isEmpty()) {
            return 0;
        }
        long approvedCount = this.approvals.stream()
                .filter(approval -> approval.getStatus() == ApprovalStatus.APPROVED)
                .count();
        return (int) ((approvedCount * 100) / this.approvals.size());
    }

    /**
     * Check if there are pending approvals.
     */
    public boolean hasPendingApprovals() {
        if (this.approvals == null || this.approvals.isEmpty()) {
            return false;
        }
        return this.approvals.stream()
                .anyMatch(approval -> approval.getStatus() == ApprovalStatus.PENDING);
    }

    /**
     * Get the next approver in the approval chain (lowest approvalOrder that is PENDING).
     */
    public String getNextApprover() {
        if (this.approvals == null || this.approvals.isEmpty()) {
            return null;
        }
        return this.approvals.stream()
                .filter(approval -> approval.getStatus() == ApprovalStatus.PENDING)
                .min(Comparator.comparingInt(ChangeApproval::getApprovalOrder))
                .map(ChangeApproval::getApproverId)
                .orElse(null);
    }

    /**
     * Add a change history entry.
     */
    public void addHistory(ChangeHistory historyEntry) {
        if (this.history == null) {
            this.history = new ArrayList<>();
        }
        historyEntry.setChange(this);
        this.history.add(historyEntry);
    }

    /**
     * Add an approval record.
     */
    public void addApproval(ChangeApproval approval) {
        if (this.approvals == null) {
            this.approvals = new ArrayList<>();
        }
        approval.setChange(this);
        this.approvals.add(approval);
    }

    /**
     * Add an affected part.
     */
    public void addAffectedPart(Part part) {
        if (this.affectedParts == null) {
            this.affectedParts = new HashSet<>();
        }
        this.affectedParts.add(part);
    }

    /**
     * Get count of pending approvals.
     */
    public long getPendingApprovalCount() {
        if (this.approvals == null) {
            return 0;
        }
        return this.approvals.stream()
                .filter(approval -> approval.getStatus() == ApprovalStatus.PENDING)
                .count();
    }

    /**
     * Get count of rejected approvals.
     */
    public long getRejectedApprovalCount() {
        if (this.approvals == null) {
            return 0;
        }
        return this.approvals.stream()
                .filter(approval -> approval.getStatus() == ApprovalStatus.REJECTED)
                .count();
    }
}
