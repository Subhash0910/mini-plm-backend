package com.sam.mini_plm_backend.entity;
import com.sam.mini_plm_backend.enums.ChangeStatus;
import com.sam.mini_plm_backend.enums.ChangeType;

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

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ChangeStatus status = ChangeStatus.DRAFT;

    // If you later need extra fields on the relationship, replace this with a join-entity.
    @ManyToMany
    @JoinTable(
            name = "change_parts",
            joinColumns = @JoinColumn(name = "change_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    @Builder.Default
    private Set<Part> affectedParts = new HashSet<>();

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

    @OneToMany(mappedBy = "change", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChangeHistory> history = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;

        if (this.type == null) {
            // Better: throw exception, but defaulting avoids NPE during early dev.
            this.type = ChangeType.ECR;
        }
        if (this.changeNumber == null || this.changeNumber.isBlank()) {
            this.changeNumber = this.type.name() + "-" + System.currentTimeMillis();
        }
    }
}
