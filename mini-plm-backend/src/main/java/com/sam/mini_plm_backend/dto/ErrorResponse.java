package com.sam.mini_plm_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized error response for all API endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * HTTP Status Code
     */
    private int status;

    /**
     * Error message for client
     */
    private String message;

    /**
     * Error code/type for frontend to handle specific errors
     */
    private String error;

    /**
     * Timestamp when error occurred
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * API path that caused the error
     */
    private String path;

    /**
     * Additional details/trace information
     */
    private String details;

    /**
     * Constructor for basic error
     */
    public ErrorResponse(int status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with details
     */
    public ErrorResponse(int status, String message, String error, String path) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
}
