package com.sam.mini_plm_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bom_lines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    private BOM bom;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "component_part_id", nullable = false)
    private Part componentPart;

    private Integer lineNumber;
    private Integer quantity;
    private String unitOfMeasure;
    private String referenceDesignator;
    private String notes;
    private Integer sequenceNumber;
}
