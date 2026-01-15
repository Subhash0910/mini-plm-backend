package com.sam.mini_plm_backend.controller;

import com.sam.mini_plm_backend.dto.*;
import com.sam.mini_plm_backend.enums.ChangeStatus;
import com.sam.mini_plm_backend.service.ChangeService;
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
@RequestMapping("/changes")
@RequiredArgsConstructor
public class ChangeController {

    private final ChangeService changeService;

    @PostMapping
    public ResponseEntity<ChangeResponse> createChange(
            @Valid @RequestBody CreateChangeRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        ChangeResponse response = changeService.createChange(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Windchill-like explicit submit step: DRAFT -> PENDING_APPROVAL.
     */
    @PostMapping("/{changeId}/submit")
    public ResponseEntity<ChangeResponse> submitChange(
            @PathVariable Long changeId,
            @Valid @RequestBody SubmitChangeRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        ChangeResponse response = changeService.submitChange(changeId, request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ChangeResponse>> getAllChanges() {
        return ResponseEntity.ok(changeService.getAllChanges());
    }

    @GetMapping("/{changeId}")
    public ResponseEntity<ChangeResponse> getChangeById(@PathVariable Long changeId) {
        return ResponseEntity.ok(changeService.getChangeById(changeId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ChangeResponse>> getChangesByStatus(@PathVariable ChangeStatus status) {
        return ResponseEntity.ok(changeService.getChangesByStatus(status));
    }

    @PostMapping("/{changeId}/approve")
    public ResponseEntity<ChangeResponse> approveChange(
            @PathVariable Long changeId,
            @Valid @RequestBody ApproveChangeRequest request,
            Authentication authentication) {
        String approverId = authentication.getName();
        ChangeResponse response = changeService.approveChange(changeId, request, approverId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{changeId}/implement")
    public ResponseEntity<ChangeResponse> implementChange(
            @PathVariable Long changeId,
            Authentication authentication) {
        String userId = authentication.getName();
        ChangeResponse response = changeService.implementChange(changeId, userId);
        return ResponseEntity.ok(response);
    }
}
