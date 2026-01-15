package com.sam.mini_plm_backend.repository;

import com.sam.mini_plm_backend.entity.BOM;
import com.sam.mini_plm_backend.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BOMRepository extends JpaRepository<BOM, Long> {
    List<BOM> findByParentPart(Part parentPart);
    List<BOM> findByParentPartAndIsActiveTrue(Part parentPart);
}
