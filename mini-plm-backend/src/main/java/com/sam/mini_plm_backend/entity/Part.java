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
}
