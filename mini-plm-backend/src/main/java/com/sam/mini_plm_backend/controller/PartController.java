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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parts")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://mini-plm-frontend.onrender.com"
})
public class PartController {

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

    @GetMapping
    public ResponseEntity<Page<PartResponse>> getAllParts(
            @RequestParam(required = false) String lifecycleState,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String partNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PartResponse> result;

        // If search parameters provided, use search
        if (name != null || partNumber != null) {
            result = partService.searchParts(name, partNumber, pageable);
        } else if (lifecycleState != null) {
            // Filter by lifecycle state
            result = partService.getAllParts(lifecycleState, pageable);
        } else {
            // Get all parts
            result = partService.getAllParts(null, pageable);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> getPartById(@PathVariable Long id) {
        PartResponse response = partService.getPartById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PartResponse> createPart(
            @Valid @RequestBody CreatePartRequest request,
            @RequestParam(defaultValue = "system") String createdBy
    ) {
        PartResponse created = partService.createPart(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePartRequest request,
            @RequestParam(defaultValue = "system") String modifiedBy
    ) {
        PartResponse updated = partService.updatePart(id, request, modifiedBy);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // LIFECYCLE ACTIONS
    // =========================

    @PostMapping("/{id}/promote")
    public ResponseEntity<LifecycleTransitionResponse> promote(
            @PathVariable Long id,
            @RequestParam(defaultValue = "system") String transitionedBy
    ) throws Exception {
        Part part = lifecycleService.promote(id, transitionedBy);
        return ResponseEntity.ok(partMapper.toTransitionResponse(part));
    }

    @PostMapping("/{id}/revise")
    public ResponseEntity<LifecycleTransitionResponse> revise(
            @PathVariable Long id,
            @RequestParam(defaultValue = "system") String transitionedBy
    ) throws Exception {
        Part part = lifecycleService.revise(id, transitionedBy);
        return ResponseEntity.ok(partMapper.toTransitionResponse(part));
    }

    @PostMapping("/{id}/obsolete")
    public ResponseEntity<LifecycleTransitionResponse> obsolete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "system") String transitionedBy
    ) throws Exception {
        Part part = lifecycleService.markObsolete(id, transitionedBy);
        return ResponseEntity.ok(partMapper.toTransitionResponse(part));
    }

    // =========================
    // HISTORY
    // =========================

    @GetMapping("/{id}/history")
    public ResponseEntity<List<StateTransitionHistory>> history(@PathVariable Long id) {
        Part part = partService.getPartEntity(id);
        List<StateTransitionHistory> histories = historyRepository.findByPartOrderByTransitionDateDesc(part);
        return ResponseEntity.ok(histories);
    }
}