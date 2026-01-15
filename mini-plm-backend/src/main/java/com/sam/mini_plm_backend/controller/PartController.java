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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

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

    /**
     * Extract the current authenticated username from JWT token
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }

    // =========================
    // BASIC CRUD
    // =========================

    @GetMapping
    public ResponseEntity<Page<PartResponse>> getAllParts(
            @RequestParam(required = false) String lifecycleState,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String partNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Fetching parts - lifecycleState: {}, name: {}, partNumber: {}, page: {}, size: {}",
                lifecycleState, name, partNumber, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<PartResponse> result;

        if (name != null || partNumber != null) {
            logger.debug("Using search filter for parts");
            result = partService.searchParts(name, partNumber, pageable);
        } else if (lifecycleState != null) {
            logger.debug("Filtering parts by lifecycle state: {}", lifecycleState);
            result = partService.getAllParts(lifecycleState, pageable);
        } else {
            logger.debug("Fetching all parts");
            result = partService.getAllParts(null, pageable);
        }

        logger.info("Retrieved {} parts", result.getTotalElements());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> getPartById(@PathVariable Long id) {
        logger.info("Fetching part with ID: {}", id);

        PartResponse response = partService.getPartById(id);
        logger.info("Part found: {}", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PartResponse> createPart(
            @Valid @RequestBody CreatePartRequest request
    ) {
        String createdBy = getCurrentUsername();
        logger.info("Creating new part: {} by user: {}", request.getPartNumber(), createdBy);

        PartResponse created = partService.createPart(request, createdBy);
        logger.info("Part created successfully with ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePartRequest request
    ) {
        String modifiedBy = getCurrentUsername();
        logger.info("Updating part ID: {} by user: {}", id, modifiedBy);

        PartResponse updated = partService.updatePart(id, request, modifiedBy);
        logger.info("Part updated successfully: {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        String deletedBy = getCurrentUsername();
        logger.info("Deleting part ID: {} by user: {}", id, deletedBy);

        partService.deletePart(id);
        logger.info("Part deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // LIFECYCLE ACTIONS
    // =========================

    @PostMapping("/{id}/promote")
    public ResponseEntity<LifecycleTransitionResponse> promote(@PathVariable Long id) throws Exception {
        String transitionedBy = getCurrentUsername();
        logger.info("Promoting part ID: {} by user: {}", id, transitionedBy);

        Part part = lifecycleService.promote(id, transitionedBy);
        logger.info("Part promoted successfully: {}", id);
        return ResponseEntity.ok(partMapper.toTransitionResponse(part));
    }

    @PostMapping("/{id}/revise")
    public ResponseEntity<LifecycleTransitionResponse> revise(@PathVariable Long id) throws Exception {
        String transitionedBy = getCurrentUsername();
        logger.info("Creating revision for part ID: {} by user: {}", id, transitionedBy);

        Part part = lifecycleService.revise(id, transitionedBy);
        logger.info("Part revision created successfully: {}", id);
        return ResponseEntity.ok(partMapper.toTransitionResponse(part));
    }

    @PostMapping("/{id}/obsolete")
    public ResponseEntity<LifecycleTransitionResponse> obsolete(@PathVariable Long id) throws Exception {
        String transitionedBy = getCurrentUsername();
        logger.info("Marking part ID: {} as obsolete by user: {}", id, transitionedBy);

        Part part = lifecycleService.markObsolete(id, transitionedBy);
        logger.info("Part marked obsolete successfully: {}", id);
        return ResponseEntity.ok(partMapper.toTransitionResponse(part));
    }

    // =========================
    // HISTORY
    // =========================

    @GetMapping("/{id}/history")
    public ResponseEntity<List<StateTransitionHistory>> history(@PathVariable Long id) {
        logger.info("Fetching state transition history for part ID: {}", id);

        Part part = partService.getPartEntity(id);
        List<StateTransitionHistory> histories =
                historyRepository.findByPartOrderByTransitionDateDesc(part);

        logger.info("Retrieved {} state transitions for part: {}", histories.size(), id);
        return ResponseEntity.ok(histories);
    }
}
