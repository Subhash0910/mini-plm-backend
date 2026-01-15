package com.sam.mini_plm_backend.exception;

public class PartNotFoundException extends RuntimeException {

    public PartNotFoundException(Long id) {
        super("Part with ID " + id + " not found");
    }
}
