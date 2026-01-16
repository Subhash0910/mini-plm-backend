package com.sam.mini_plm_backend.entity;

import com.sam.mini_plm_backend.enums.LifecycleState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "parts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String partNumber;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LifecycleState lifecycleState;

    @Column(nullable = false)
    private Integer revisionNumber;

    /**
     * Stored revision string (e.g., "A", "B", "1.0" depending on your chosen scheme).
     */
    @Column(nullable = false)
    private String revisionSequence;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private LocalDateTime releasedDate;
    private LocalDateTime obsoleteDate;

    private String createdBy;
    private String lastModifiedBy;

    private Boolean isDeleted;
    private Boolean isAssembly;

    /**
     * Backward-compatible API used by LifecycleService.
     * Maps revisionLetter to revisionSequence.
     */
    public void setRevisionLetter(String revisionLetter) {
        this.revisionSequence = revisionLetter;
    }

    /**
     * Backward-compatible API used by older code.
     */
    public String getRevisionLetter() {
        return this.revisionSequence;
    }
}
