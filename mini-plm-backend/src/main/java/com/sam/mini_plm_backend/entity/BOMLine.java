package com.sam.mini_plm_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bom_lines")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    private BOM bom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_part_id", nullable = false)
    private Part componentPart;  // The sub-part

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;  // 10, 20, 30... for sorting

    @Column(name = "quantity", nullable = false)
    private Double quantity;  // How many needed

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;  // "EA" (each), "KG", etc.

    @Column(name = "reference_designator")
    private String referenceDesignator;  // e.g., "R1", "C2" for electronics

    @Column(name = "notes")
    private String notes;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;  // Assembly sequence order
}
