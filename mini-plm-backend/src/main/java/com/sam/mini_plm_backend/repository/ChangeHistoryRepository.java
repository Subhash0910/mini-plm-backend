package com.sam.mini_plm_backend.repository;

import com.sam.mini_plm_backend.entity.Change;
import com.sam.mini_plm_backend.entity.ChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChangeHistoryRepository extends JpaRepository<ChangeHistory, Long> {
    List<ChangeHistory> findByChange(Change change);
    List<ChangeHistory> findByPerformedBy(String performedBy);
    List<ChangeHistory> findByChangeOrderByCreatedAtDesc(Change change);
}
