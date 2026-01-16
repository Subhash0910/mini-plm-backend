package com.sam.miniplmbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "boms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_part_id", nullable = false)
    private Part parentPart;

    @Column(nullable = false)
    private String bomName;

    @Column(nullable = false)
    private String bomVersion;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Boolean isActive;

    private String createdBy;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BOMLine> bomLines;
}
