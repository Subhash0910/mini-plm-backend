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
 * Part Entity
 * Represents a product part/component in the PLM system
 */
@Entity
@Table(name = "PART", indexes = {
    @Index(name = "idx_part_number", columnList = "part_number", unique = true),
    @Index(name = "idx_part_created", columnList = "created_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "part_number", unique = true, nullable = false, length = 100)
    private String partNumber;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_assembly")
    private Boolean isAssembly = false;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;
    
    @OneToMany(mappedBy = "parentPart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BOM> boms = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
        if (isAssembly == null) {
            isAssembly = false;
        }
    }
}