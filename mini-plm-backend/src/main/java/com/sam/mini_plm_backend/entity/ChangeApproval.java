package com.sam.mini_plm_backend.entity;

import com.sam.mini_plm_backend.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "change_approvals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_id", nullable = false)
    private Change change;

    @Column(name = "approver_id", nullable = false)
    private String approverId;  // User who should approve

    @Column(name = "approval_order", nullable = false)
    private Integer approvalOrder;  // 1st level, 2nd level, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(name = "comments")
    private String comments;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = ApprovalStatus.PENDING;
        }
    }
}
