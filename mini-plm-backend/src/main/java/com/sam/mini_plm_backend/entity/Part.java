package com.sam.mini_plm_backend.entity;

import com.sam.mini_plm_backend.enums.LifecycleState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "parts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_part_revision",
                        columnNames = {"part_number", "revision_sequence"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // Business Identity
    // ===============================

    @Column(name = "part_number", nullable = false)
    private String partNumber;

    private String name;
    private String description;

    // Optional legacy column
    private String version;

    // ===============================
    // Lifecycle
    // ===============================

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_state", nullable = false)
    @Builder.Default
    private LifecycleState lifecycleState = LifecycleState.IN_WORK;

    // Revision tracking
    @Column(name = "revision_number", nullable = false)
    @Builder.Default
    private Integer revisionNumber = 1;

    @Column(name = "revision_letter")
    private String revisionLetter;

    // Example: 1.0 , 2.0 , A.1 , etc
    @Column(name = "revision_sequence", nullable = false)
    @Builder.Default
    private String revisionSequence = "1.0";

    // ===============================
    // Dates
    // ===============================

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "released_date")
    private LocalDateTime releasedDate;

    @Column(name = "obsolete_date")
    private LocalDateTime obsoleteDate;

    // ===============================
    // Audit
    // ===============================

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    // Soft delete like Windchill
    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // ===============================
    // Lifecycle hooks
    // ===============================

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.isDeleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_part_id")
    private Part parentPart;

    @OneToMany(mappedBy = "parentPart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Part> subParts = new ArrayList<>();

    @Column(name = "quantity_required")
    private Double quantityRequired; // For sub-parts

    @Column(name = "is_assembly")
    @Builder.Default
    private Boolean isAssembly = false;


    // ===============================
    // Business Rules
    // ===============================

    public boolean isEditable() {
        return lifecycleState != null && lifecycleState.isEditable();
    }

    public boolean isUsableInBOM() {
        return lifecycleState != null && lifecycleState.isUsableInBOM();
    }
}
