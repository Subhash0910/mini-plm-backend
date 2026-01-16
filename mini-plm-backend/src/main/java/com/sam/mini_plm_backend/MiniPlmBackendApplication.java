package com.sam.mini_plm_backend;

/**
 * Backward-compatible entry point.
 * This delegates to the canonical Spring Boot application: com.miniplm.MiniPlmApplication.
 */
public class MiniPlmBackendApplication {

    public static void main(String[] args) {
        com.miniplm.MiniPlmApplication.main(args);
    }
}
