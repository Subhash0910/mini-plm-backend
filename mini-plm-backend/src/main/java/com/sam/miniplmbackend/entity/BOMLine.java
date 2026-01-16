package com.sam.miniplmbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BOM_LINE")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bom_id")
    private BOM bom;

    @ManyToOne
    @JoinColumn(name = "component_part_id")
    private Part componentPart;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @Column(name = "reference_designator")
    private String referenceDesignator;

    @Column(name = "notes")
    private String notes;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;
}