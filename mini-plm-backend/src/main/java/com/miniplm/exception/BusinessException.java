package com.miniplm.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Custom exception for business logic errors.
 * Extends RuntimeException for unchecked exception handling.
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {

    private String errorCode;
    private String errorMessage;

    public BusinessException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }
}
