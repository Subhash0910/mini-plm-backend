package com.sam.mini_plm_backend.features.document.service;

import com.sam.mini_plm_backend.common.exception.ResourceNotFoundException;
import com.sam.mini_plm_backend.common.exception.ValidationException;
import com.sam.mini_plm_backend.common.util.ValidationUtil;
import com.sam.mini_plm_backend.features.document.dto.DocumentCreateDTO;
import com.sam.mini_plm_backend.features.document.dto.DocumentResponseDTO;
import com.sam.mini_plm_backend.features.document.dto.DocumentUpdateDTO;
import com.sam.mini_plm_backend.features.document.entity.Document;
import com.sam.mini_plm_backend.features.document.mapper.DocumentMapper;
import com.sam.mini_plm_backend.features.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementation for Document operations
 * Implements business logic for document management
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    @Override
    public DocumentResponseDTO createDocument(DocumentCreateDTO dto, String username) {
        log.info("Creating new document: {}", dto.getDocumentNumber());

        // Validate input
        ValidationUtil.validateNotEmpty(dto.getDocumentNumber(), "Document Number");
        ValidationUtil.validateNotEmpty(dto.getTitle(), "Title");

        // Check if document already exists
        if (documentRepository.findByDocumentNumber(dto.getDocumentNumber()).isPresent()) {
            throw new ValidationException("Document with number " + dto.getDocumentNumber() + " already exists");
        }

        // Create and save document
        Document document = Document.builder()
                .documentNumber(dto.getDocumentNumber())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .documentType(dto.getDocumentType())
                .revision(dto.getRevision() != null ? dto.getRevision() : "1.0")
                .createdBy(username)
                .build();

        Document savedDocument = documentRepository.save(document);
        log.info("Document created successfully with ID: {}", savedDocument.getId());

        return documentMapper.toResponseDTO(savedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponseDTO getDocumentById(Long id) {
        log.info("Fetching document by ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));
        return documentMapper.toResponseDTO(document);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponseDTO getDocumentByNumber(String documentNumber) {
        log.info("Fetching document by number: {}", documentNumber);
        Document document = documentRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with number: " + documentNumber));
        return documentMapper.toResponseDTO(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponseDTO> getAllDocuments(Pageable pageable) {
        log.info("Fetching all documents");
        return documentRepository.findAll(pageable).map(documentMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponseDTO> getDocumentsByState(String state, Pageable pageable) {
        log.info("Fetching documents by state: {}", state);
        return documentRepository.findByState(state, pageable).map(documentMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponseDTO> searchDocuments(String searchText, String state, Pageable pageable) {
        log.info("Searching documents with text: {}, state: {}", searchText, state);
        return documentRepository.searchByStateAndTitle(state, searchText, pageable)
                .map(documentMapper::toResponseDTO);
    }

    @Override
    public DocumentResponseDTO updateDocument(Long id, DocumentUpdateDTO dto, String username) {
        log.info("Updating document with ID: {}", id);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        if (dto.getTitle() != null) {
            document.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            document.setDescription(dto.getDescription());
        }
        if (dto.getDocumentType() != null) {
            document.setDocumentType(dto.getDocumentType());
        }

        document.setModifiedBy(username);
        document.setModifiedDate(LocalDateTime.now());

        Document updatedDocument = documentRepository.save(document);
        log.info("Document updated successfully with ID: {}", id);

        return documentMapper.toResponseDTO(updatedDocument);
    }

    @Override
    public void releaseDocument(Long id, String username) {
        log.info("Releasing document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        document.setState("RELEASED");
        document.setModifiedBy(username);
        document.setModifiedDate(LocalDateTime.now());
        documentRepository.save(document);

        log.info("Document released successfully with ID: {}", id);
    }

    @Override
    public void obsoleteDocument(Long id, String username) {
        log.info("Making document obsolete with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        document.setState("OBSOLETE");
        document.setModifiedBy(username);
        document.setModifiedDate(LocalDateTime.now());
        documentRepository.save(document);

        log.info("Document made obsolete with ID: {}", id);
    }

    @Override
    public void deleteDocument(Long id) {
        log.info("Deleting document with ID: {}", id);
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Document not found with ID: " + id);
        }
        documentRepository.deleteById(id);
        log.info("Document deleted successfully with ID: {}", id);
    }
}
