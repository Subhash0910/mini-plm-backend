package com.sam.mini_plm_backend.features.document.repository;

import com.sam.mini_plm_backend.features.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Document entity
 * Handles all database operations for documents
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByDocumentNumber(String documentNumber);

    Page<Document> findByState(String state, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.state = :state AND d.title LIKE %:searchText% ORDER BY d.createdDate DESC")
    Page<Document> searchByStateAndTitle(@Param("state") String state, @Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.state = :state")
    long countByState(@Param("state") String state);
}
