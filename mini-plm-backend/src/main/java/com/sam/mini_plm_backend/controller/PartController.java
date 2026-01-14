package com.sam.mini_plm_backend.controller;

import com.sam.mini_plm_backend.Model.Part;
import com.sam.mini_plm_backend.enums.LifecycleState;
import com.sam.mini_plm_backend.repository.PartRepository;
import com.sam.mini_plm_backend.repository.StateTransitionHistoryRepository;
import com.sam.mini_plm_backend.service.LifecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://mini-plm-frontend.onrender.com"
})
public class PartController {

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private LifecycleService lifecycleService;

    @Autowired
    private StateTransitionHistoryRepository historyRepository;

    // =========================
    // BASIC CRUD
    // =========================

    @GetMapping
    public List<Part> getAllParts(@RequestParam(required = false) String lifecycleState) {
        if (lifecycleState != null && !lifecycleState.isEmpty()) {
            LifecycleState state = LifecycleState.valueOf(lifecycleState);
            return partRepository.findByLifecycleState(state);
        }
        return partRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPartById(@PathVariable Long id) {
        return partRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createPart(
            @RequestBody Part part,
            @RequestParam(defaultValue = "system") String createdBy
    ) {
        try {
            part.setLifecycleState(LifecycleState.IN_WORK);
            part.setRevisionNumber(1);
            part.setRevisionSequence("1.0");

            part.setCreatedBy(createdBy);
            part.setLastModifiedBy(createdBy);

            part.setCreatedDate(LocalDateTime.now());
            part.setLastModifiedDate(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.CREATED).body(partRepository.save(part));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Create failed: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePart(
            @PathVariable Long id,
            @RequestBody Part updatedPart,
            @RequestParam(defaultValue = "system") String modifiedBy
    ) {
        return partRepository.findById(id).map(existing -> {
            if (existing.getLifecycleState() != LifecycleState.IN_WORK) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Edit allowed only in IN_WORK.");
            }

            // Don’t allow changing identity/revision fields from UI
            existing.setName(updatedPart.getName());
            existing.setDescription(updatedPart.getDescription());
            existing.setVersion(updatedPart.getVersion());

            existing.setLastModifiedBy(modifiedBy);
            existing.setLastModifiedDate(LocalDateTime.now());

            return ResponseEntity.ok(partRepository.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePart(@PathVariable Long id) {
        return partRepository.findById(id).map(part -> {
            if (part.getLifecycleState() != LifecycleState.IN_WORK) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Delete allowed only in IN_WORK. Use Obsolete instead.");
            }
            partRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =========================
    // LIFECYCLE ACTIONS
    // =========================

    @PostMapping("/{id}/promote")
    public ResponseEntity<?> promote(
            @PathVariable Long id,
            @RequestParam(defaultValue = "system") String transitionedBy
    ) {
        try {
            return ResponseEntity.ok(lifecycleService.promote(id, transitionedBy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Promote failed: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/revise")
    public ResponseEntity<?> revise(
            @PathVariable Long id,
            @RequestParam(defaultValue = "system") String transitionedBy
    ) {
        try {
            return ResponseEntity.ok(lifecycleService.revise(id, transitionedBy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Revise failed: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/obsolete")
    public ResponseEntity<?> obsolete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "system") String transitionedBy
    ) {
        try {
            return ResponseEntity.ok(lifecycleService.markObsolete(id, transitionedBy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Obsolete failed: " + e.getMessage());
        }
    }

    // =========================
    // HISTORY
    // =========================

    @GetMapping("/{id}/history")
    public ResponseEntity<?> history(@PathVariable Long id) {
        return partRepository.findById(id)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(
                        historyRepository.findByPartOrderByTransitionDateDesc(p)
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
