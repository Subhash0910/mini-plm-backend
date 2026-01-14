package com.sam.mini_plm_backend.repository;

import com.sam.mini_plm_backend.entity.Part;
import com.sam.mini_plm_backend.entity.StateTransitionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StateTransitionHistoryRepository extends JpaRepository<StateTransitionHistory, Long> {
    List<StateTransitionHistory> findByPartOrderByTransitionDateDesc(Part part);
}