package com.sam.mini_plm_backend.repository;

import com.sam.mini_plm_backend.entity.Change;
import com.sam.mini_plm_backend.entity.ChangeApproval;
import com.sam.mini_plm_backend.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChangeApprovalRepository extends JpaRepository<ChangeApproval, Long> {
    List<ChangeApproval> findByChange(Change change);
    Optional<ChangeApproval> findByChangeAndApproverIdAndStatus(Change change, String approverId, ApprovalStatus status);
    List<ChangeApproval> findByApproverId(String approverId);
}
