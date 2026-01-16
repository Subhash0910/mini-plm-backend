package com.sam.mini_plm_backend.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * BOM (Bill of Materials) Entity
 * Represents a BOM with parent part and child components
 */
@Entity
@Table(name = "BOM", indexes = {@Index(name = "idx_bom_name", columnList = "bom_name")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOM {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_part_id", nullable = false)
    private Part parentPart;
    
    @Column(name = "bom_name", nullable = false, length = 255)
    private String bomName;
    
    @Column(name = "bom_version", nullable = false, length = 50)
    private String bomVersion;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;
    
    @OneToMany(mappedBy = "bom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BOMLine> bomLines = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}