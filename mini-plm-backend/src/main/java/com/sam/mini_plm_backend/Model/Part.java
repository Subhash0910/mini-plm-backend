package com.sam.mini_plm_backend.Model;

import com.sam.mini_plm_backend.enums.LifecycleState;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "parts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_part_revision", columnNames = {"part_number", "revision_sequence"})
        }
)
@Data
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Business Identity
    @Column(name = "part_number", nullable = false)
    private String partNumber;

    private String name;
    private String description;

    // (optional) legacy column - can remove later
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_state", nullable = false)
    private LifecycleState lifecycleState = LifecycleState.IN_WORK;

    @Column(name = "revision_number", nullable = false)
    private Integer revisionNumber = 1;

    @Column(name = "revision_letter")
    private String revisionLetter;

    @Column(name = "revision_sequence", nullable = false)
    private String revisionSequence = "1.0";

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @Column(name = "released_date")
    private LocalDateTime releasedDate;

    @Column(name = "obsolete_date")
    private LocalDateTime obsoleteDate;

    private String createdBy;
    private String lastModifiedBy;

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }

    public boolean isEditable() {
        return lifecycleState != null && lifecycleState.isEditable();
    }

    public boolean isUsableInBOM() {
        return lifecycleState != null && lifecycleState.isUsableInBOM();
    }
}
