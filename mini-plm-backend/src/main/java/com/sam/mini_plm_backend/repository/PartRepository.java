package com.sam.mini_plm_backend.repository;

import com.sam.mini_plm_backend.Model.Part;
import com.sam.mini_plm_backend.enums.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findByLifecycleState(LifecycleState lifecycleState);

    List<Part> findByPartNumberOrderByRevisionNumberDesc(String partNumber);
}
