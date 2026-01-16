package com.sam.mini_plm_backend.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * BOMLine Entity
 * Represents a line item in a Bill of Materials
 */
@Entity
@Table(name = "BOM_LINE", indexes = {
    @Index(name = "idx_bomline_bom", columnList = "bom_id"),
    @Index(name = "idx_bomline_component", columnList = "component_part_id")
})
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
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_part_id", nullable = false)
    private Part componentPart;
    
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;
    
    @Column(name = "quantity", nullable = false, precision = 10, scale = 4)
    private BigDecimal quantity;
    
    @Column(name = "unit_of_measure", length = 50)
    private String unitOfMeasure;
    
    @Column(name = "reference_designator", length = 100)
    private String referenceDesignator;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "sequence_number")
    private Integer sequenceNumber;
}