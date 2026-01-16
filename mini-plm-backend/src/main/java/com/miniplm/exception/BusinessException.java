package com.miniplm.exception;

/**
 * Base Business Exception
 * Represents application-level business logic exceptions.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
