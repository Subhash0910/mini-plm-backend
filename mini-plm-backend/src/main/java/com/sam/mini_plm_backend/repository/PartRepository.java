package com.sam.mini_plm_backend.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sam.mini_plm_backend.entity.Part;
import com.sam.mini_plm_backend.enums.LifecycleState;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findByLifecycleState(LifecycleState lifecycleState);

    List<Part> findByPartNumberOrderByRevisionNumberDesc(String partNumber);

    Page<Part> findByIsDeletedFalse(Pageable pageable);

    Page<Part> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable);

    Page<Part> findByPartNumberContainingIgnoreCaseAndIsDeletedFalse(String partNumber, Pageable pageable);

    Page<Part> findByNameContainingIgnoreCaseAndPartNumberContainingIgnoreCaseAndIsDeletedFalse(
            String name,
            String partNumber,
            Pageable pageable
    );
}
