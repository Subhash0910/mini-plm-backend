package com.sam.mini_plm_backend.controller;

import com.sam.mini_plm_backend.dto.CreatePartRequest;
import com.sam.mini_plm_backend.dto.LifecycleTransitionResponse;
import com.sam.mini_plm_backend.dto.PartResponse;
import com.sam.mini_plm_backend.dto.UpdatePartRequest;
import com.sam.mini_plm_backend.entity.Part;
import com.sam.mini_plm_backend.entity.StateTransitionHistory;
import com.sam.mini_plm_backend.repository.StateTransitionHistoryRepository;
import com.sam.mini_plm_backend.service.LifecycleService;
import com.sam.mini_plm_backend.service.PartMapper;
import com.sam.mini_plm_backend.service.PartService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API Controller for Part Management
 * Base path: /api/parts
 * 
 * CORS is centralized in SecurityConfig - no need for @CrossOrigin here
 */
@RestController
@RequestMapping("/api/parts")
public class PartController {

    private static final Logger logger = LoggerFactory.getLogger(PartController.class);

    private final LifecycleService lifecycleService;
    private final StateTransitionHistoryRepository historyRepository;
    private final PartService partService;
    private final PartMapper partMapper;

    public PartController(
            LifecycleService lifecycleService,
            StateTransitionHistoryRepository historyRepository,
            PartService partService,
            PartMapper partMapper
    ) {
        this.lifecycleService = lifecycleService;
        this.historyRepository = historyRepository;
        this.partService = partService;
        this.partMapper = partMapper;
    }

    // =========================
    // BASIC CRUD
    // =========================

    /**
     * Get all parts with optional filtering and pagination
     */
    @GetMapping
    public ResponseEntity<Page<PartResponse>> getAllParts(
            @RequestParam(required = false) String lifecycleState,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String partNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("GET /api/parts - page: {}, size: {}, lifecycleState: {}, name: {}, partNumber: {}",
                page, size, lifecycleState, name, partNumber);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PartResponse> result;

        // If search parameters provided, use search
        if (name != null || partNumber != null) {
            logger.debug("Searching parts by name or partNumber");
            result = partService.searchParts(name, partNumber, pageable);
        } else if (lifecycleState != null) {
            // Filter by lifecycle state
            logger.debug("Filtering parts by lifecycle state: {}", lifecycleState);
            result = partService.getAllParts(lifecycleState, pageable);
        } else {
            // Get all parts
            logger.debug("Fetching all parts");
            result = partService.getAllParts(null, pageable);
        }

        logger.info("Returning {} parts (page {}/{})", result.getNumberOfElements(), page, result.getTotalPages());
        return ResponseEntity.ok(result);
    }

    /**
     * Get part by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> getPartById(@PathVariable Long id) {
        logger.info("GET /api/parts/{}", id);
        PartResponse response = partService.getPartById(id);
        logger.info("Part found: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new part
     */
    @PostMapping
    public ResponseEntity<PartResponse> createPart(
            @Valid @RequestBody CreatePartRequest request,
            @RequestParam(defaultValue = "system") String createdBy
    ) {
        logger.info("POST /api/parts - Creating new part: {} by {}", request.getPartNumber(), createdBy);
        PartResponse created = partService.createPart(request, createdBy);
        logger.info("Part created successfully - ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing part
     */
    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePartRequest request,
            @RequestParam(defaultValue = "system") String modifiedBy
    ) {
        logger.info("PUT /api/parts/{} - Updating part by {}", id, modifiedBy);
        PartResponse updated = partService.updatePart(id, request, modifiedBy);
        logger.info("Part updated successfully - ID: {}", id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete (soft delete) a part
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        logger.info("DELETE /api/parts/{} - Deleting part", id);
        partService.deletePart(id);
        logger.info("Part soft-deleted successfully - ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // LIFECYCLE ACTIONS
    // =========================

    /**
     * Promote part to next lifecycle state
     */
    @PostMapping("/{id}/promote")
    public ResponseEntity<LifecycleTransitionResponse> promote(
            @PathVariable Long id,
            @RequestParam(defaultValue = "system") String transitionedBy
    ) throws Exception {
        logger.info("POST /api/parts/{}/promote - Promoting part by {}", id, transitionedBy);
        Part part = lifecycleService.promote(id, transitionedBy);
        logger.info("Part promoted successfully - ID: {}", id);
        return ResponseEntity.ok(partMapper.toTransitionResponse(part));
    }

    /**
     * Revise part - create new revision
     */
    @PostMapping("/{id}/revise")
    public ResponseEntity<LifecycleTransitionResponse> revise(
            @PathVariable Long id,
            @RequestParam(defaultValue = "system") String transitionedBy
    ) throws Exception {
        logger.info("POST /api/parts/{}/revise - Revising part by {}", id, transitionedBy);
        Part part = lifecycleService.revise(id, transitionedBy);
        logger.info("Part revised successfully - ID: {}", id);
        return ResponseEntity.ok(partMapper.toTransitionResponse(part));
    }

    /**
     * Mark part as obsolete
     */
    @PostMapping("/{id}/obsolete")
    public ResponseEntity<LifecycleTransitionResponse> obsolete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "system") String transitionedBy
    ) throws Exception {
        logger.info("POST /api/parts/{}/obsolete - Marking part as obsolete by {}", id, transitionedBy);
        Part part = lifecycleService.markObsolete(id, transitionedBy);
        logger.info("Part marked obsolete successfully - ID: {}", id);
        return ResponseEntity.ok(partMapper.toTransitionResponse(part));
    }

    // =========================
    // HISTORY
    // =========================

    /**
     * Get state transition history for a part
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<StateTransitionHistory>> history(@PathVariable Long id) {
        logger.info("GET /api/parts/{}/history - Fetching transition history", id);
        Part part = partService.getPartEntity(id);
        List<StateTransitionHistory> histories = historyRepository.findByPartOrderByTransitionDateDesc(part);
        logger.info("Retrieved {} history records for part: {}", histories.size(), id);
        return ResponseEntity.ok(histories);
    }
}
