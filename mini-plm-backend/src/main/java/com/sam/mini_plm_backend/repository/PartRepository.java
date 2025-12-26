package com.sam.mini_plm_backend.repository;

import com.sam.mini_plm_backend.Model.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {
}
