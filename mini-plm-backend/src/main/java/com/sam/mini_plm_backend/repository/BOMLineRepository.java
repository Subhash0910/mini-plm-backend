package com.sam.mini_plm_backend.repository;

import com.sam.mini_plm_backend.entity.BOM;
import com.sam.mini_plm_backend.entity.BOMLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BOMLineRepository extends JpaRepository<BOMLine, Long> {
    List<BOMLine> findByBom(BOM bom);
}
