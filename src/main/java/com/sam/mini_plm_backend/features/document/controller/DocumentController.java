package com.sam.mini_plm_backend.features.document.controller;

import com.sam.mini_plm_backend.common.constant.AppConstants;
import com.sam.mini_plm_backend.common.dto.ApiResponse;
import com.sam.mini_plm_backend.features.document.dto.DocumentCreateDTO;
import com.sam.mini_plm_backend.features.document.dto.DocumentResponseDTO;
import com.sam.mini_plm_backend.features.document.dto.DocumentUpdateDTO;
import com.sam.mini_plm_backend.features.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST Controller for Document operations
 * Handles HTTP requests for document management
 */
@RestController
@RequestMapping(AppConstants.API_PREFIX + "/documents")
@RequiredArgsConstructor
@Validated
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<ApiResponse<DocumentResponseDTO>> createDocument(
            @Valid @RequestBody DocumentCreateDTO dto) {
        log.info("POST /api/v1/documents - Creating new document");
        DocumentResponseDTO result = documentService.createDocument(dto, "currentUser");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Document created successfully", result, HttpStatus.CREATED.value()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponseDTO>> getDocumentById(@PathVariable Long id) {
        log.info("GET /api/v1/documents/{} - Fetching document", id);
        DocumentResponseDTO result = documentService.getDocumentById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Document retrieved successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/number/{documentNumber}")
    public ResponseEntity<ApiResponse<DocumentResponseDTO>> getDocumentByNumber(
            @PathVariable String documentNumber) {
        log.info("GET /api/v1/documents/number/{} - Fetching document", documentNumber);
        DocumentResponseDTO result = documentService.getDocumentByNumber(documentNumber);
        return ResponseEntity.ok(
                ApiResponse.success("Document retrieved successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DocumentResponseDTO>>> getAllDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/v1/documents - Fetching all documents");
        Pageable pageable = PageRequest.of(page, Math.min(size, AppConstants.MAX_PAGE_SIZE));
        Page<DocumentResponseDTO> result = documentService.getAllDocuments(pageable);
        return ResponseEntity.ok(
                ApiResponse.success("Documents retrieved successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<ApiResponse<Page<DocumentResponseDTO>>> getDocumentsByState(
            @PathVariable String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/v1/documents/state/{} - Fetching documents", state);
        Pageable pageable = PageRequest.of(page, Math.min(size, AppConstants.MAX_PAGE_SIZE));
        Page<DocumentResponseDTO> result = documentService.getDocumentsByState(state, pageable);
        return ResponseEntity.ok(
                ApiResponse.success("Documents retrieved successfully", result, HttpStatus.OK.value()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponseDTO>> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentUpdateDTO dto) {
        log.info("PUT /api/v1/documents/{} - Updating document", id);
        DocumentResponseDTO result = documentService.updateDocument(id, dto, "currentUser");
        return ResponseEntity.ok(
                ApiResponse.success("Document updated successfully", result, HttpStatus.OK.value()));
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<ApiResponse<Object>> releaseDocument(@PathVariable Long id) {
        log.info("POST /api/v1/documents/{}/release - Releasing document", id);
        documentService.releaseDocument(id, "currentUser");
        return ResponseEntity.ok(
                ApiResponse.success("Document released successfully", null, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteDocument(@PathVariable Long id) {
        log.info("DELETE /api/v1/documents/{} - Deleting document", id);
        documentService.deleteDocument(id);
        return ResponseEntity.ok(
                ApiResponse.success("Document deleted successfully", null, HttpStatus.OK.value()));
    }
}
