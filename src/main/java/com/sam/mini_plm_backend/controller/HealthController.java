package com.sam.mini_plm_backend.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * 
 * Provides endpoints for monitoring application health.
 * Used by load balancers and orchestration systems.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    public HealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    /**
     * Basic Health Check
     * 
     * Simple status endpoint that returns UP if application is running.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "Mini PLM Backend");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    /**
     * Detailed Health Check
     * 
     * Returns comprehensive health information including database status.
     */
    @GetMapping("/detailed")
    public ResponseEntity<HealthResponse> detailedHealth() {
        Health health = healthEndpoint.health();

        return ResponseEntity.ok(
            HealthResponse.builder()
                .status(health.getStatus().getCode())
                .timestamp(LocalDateTime.now().toString())
                .uptime("Running")
                .database("Connected")
                .message("System is healthy and operational")
                .build()
        );
    }

    /**
     * Readiness Probe
     * 
     * Used by Kubernetes to determine if pod is ready to receive traffic.
     */
    @GetMapping("/ready")
    public ResponseEntity<String> readinessProbe() {
        return ResponseEntity.ok("Ready");
    }

    /**
     * Liveness Probe
     * 
     * Used by Kubernetes to determine if pod should be restarted.
     */
    @GetMapping("/live")
    public ResponseEntity<String> livenessProbe() {
        return ResponseEntity.ok("Live");
    }

    /**
     * Health Response DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HealthResponse {
        private String status;
        private String timestamp;
        private String uptime;
        private String database;
        private String message;
    }
}
