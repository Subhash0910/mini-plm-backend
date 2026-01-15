package com.sam.mini_plm_backend.repository;

import com.sam.mini_plm_backend.entity.Change;
import com.sam.mini_plm_backend.enums.ChangeStatus;
import com.sam.mini_plm_backend.enums.ChangeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChangeRepository extends JpaRepository<Change, Long> {

    /**
     * Find change by change number (unique identifier)
     */
    Optional<Change> findByChangeNumber(String changeNumber);

    /**
     * Find all changes by status
     */
    List<Change> findByStatus(ChangeStatus status);

    /**
     * Find all changes created by a user
     */
    List<Change> findByCreatedBy(String createdBy);

    /**
     * Find all changes assigned to a user
     */
    List<Change> findByAssignedTo(String assignedTo);

    /**
     * Find all changes by type
     */
    List<Change> findByType(ChangeType type);

    /**
     * Find all changes created after a date
     */
    List<Change> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * Find all changes with due dates in a range
     */
    List<Change> findByStatusAndDueDateBetween(
            ChangeStatus status, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all pending changes for approval (ordered by creation date)
     */
    List<Change> findByStatusOrderByCreatedAtDesc(ChangeStatus status);

    /**
     * Find all approved changes (ordered by approval date)
     */
    List<Change> findByStatusOrderByApprovedAtDesc(ChangeStatus status);

    /**
     * Find changes created by user and with specific status
     */
    List<Change> findByCreatedByAndStatus(String createdBy, ChangeStatus status);

    /**
     * Find changes assigned to user with specific status
     */
    List<Change> findByAssignedToAndStatus(String assignedTo, ChangeStatus status);

    /**
     * Find changes due within date range
     */
    List<Change> findByDueDateBetweenOrderByDueDateAsc(
            LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all critical priority changes
     */
    List<Change> findByPriorityNotNull();

    /**
     * Find changes by created by and priority
     */
    List<Change> findByCreatedByAndStatusAndPriorityOrderByCreatedAtDesc(
            String createdBy, ChangeStatus status, String priority);
}
