package com.sam.miniplmbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.sam.miniplmbackend.enums.LifecycleState;

@Entity
@Table(name = "PART")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "part_number", unique = true, nullable = false)
    private String partNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_state")
    private LifecycleState lifecycleState;

    @Column(name = "revision_number")
    private Integer revisionNumber;

    @Column(name = "revision_sequence")
    private String revisionSequence;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "released_date")
    private LocalDateTime releasedDate;

    @Column(name = "obsolete_date")
    private LocalDateTime obsoleteDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "is_assembly")
    private Boolean isAssembly = false;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastModifiedDate = LocalDateTime.now();
        if (revisionNumber == null) revisionNumber = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }
}