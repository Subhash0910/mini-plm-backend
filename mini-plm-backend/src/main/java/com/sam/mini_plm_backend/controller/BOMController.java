package com.sam.mini_plm_backend.controller;

import com.sam.mini_plm_backend.dto.*;
import com.sam.mini_plm_backend.service.BomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// NOTE: server.servlet.context-path=/api already prefixes all endpoints.
// Keep controller mappings relative to the context-path to avoid /api/api/*.
@RequestMapping("/bom")
@RequiredArgsConstructor
public class BOMController {

    private final BomService bomService;

    @PostMapping
    public ResponseEntity<BOMResponse> createBOM(
            @Valid @RequestBody CreateBOMRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        BOMResponse response = bomService.createBOM(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{bomId}")
    public ResponseEntity<BOMResponse> getBOMById(@PathVariable Long bomId) {
        return ResponseEntity.ok(bomService.getBOMById(bomId));
    }

    @GetMapping("/part/{partId}/active")
    public ResponseEntity<BOMResponse> getActiveBOMForPart(@PathVariable Long partId) {
        return ResponseEntity.ok(bomService.getActiveBOMForPart(partId));
    }

    @GetMapping("/part/{partId}/all")
    public ResponseEntity<List<BOMResponse>> getAllBOMsForPart(@PathVariable Long partId) {
        return ResponseEntity.ok(bomService.getAllBOMsForPart(partId));
    }

    @PutMapping("/{bomId}")
    public ResponseEntity<BOMResponse> updateBOM(
            @PathVariable Long bomId,
            @Valid @RequestBody CreateBOMRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        BOMResponse response = bomService.updateBOM(bomId, request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bomId}/flattened")
    public ResponseEntity<List<BOMLineDto>> getFlattenedBOM(@PathVariable Long bomId) {
        return ResponseEntity.ok(bomService.getFlattenedBOM(bomId));
    }
}
