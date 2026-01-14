package com.sam.mini_plm_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import com.sam.mini_plm_backend.enums.ChangeStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "change_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // JsonIgnore prevents infinite recursion when returning Change -> history -> change -> history...
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "change_id", nullable = false)
    private Change change;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private ChangeStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private ChangeStatus newStatus;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }
}
