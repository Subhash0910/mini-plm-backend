package com.sam.mini_plm_backend.exception;

/**
 * BusinessException - Custom exception for business logic errors
 * Used when business rules are violated or invalid operations are attempted
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}