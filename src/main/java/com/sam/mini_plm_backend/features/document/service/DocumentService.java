package com.sam.mini_plm_backend.features.document.service;

import com.sam.mini_plm_backend.features.document.dto.DocumentCreateDTO;
import com.sam.mini_plm_backend.features.document.dto.DocumentResponseDTO;
import com.sam.mini_plm_backend.features.document.dto.DocumentUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Document operations
 * Defines contract for document business logic
 */
public interface DocumentService {

    DocumentResponseDTO createDocument(DocumentCreateDTO dto, String username);

    DocumentResponseDTO getDocumentById(Long id);

    DocumentResponseDTO getDocumentByNumber(String documentNumber);

    Page<DocumentResponseDTO> getAllDocuments(Pageable pageable);

    Page<DocumentResponseDTO> getDocumentsByState(String state, Pageable pageable);

    Page<DocumentResponseDTO> searchDocuments(String searchText, String state, Pageable pageable);

    DocumentResponseDTO updateDocument(Long id, DocumentUpdateDTO dto, String username);

    void releaseDocument(Long id, String username);

    void obsoleteDocument(Long id, String username);

    void deleteDocument(Long id);
}
