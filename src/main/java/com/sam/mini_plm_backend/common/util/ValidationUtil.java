package com.sam.mini_plm_backend.common.util;

import com.sam.mini_plm_backend.common.exception.ValidationException;

/**
 * Utility class for common validations
 */
public class ValidationUtil {

    public static void validateNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static void validateStringLength(String value, int minLength, int maxLength, String fieldName) {
        if (value == null || value.length() < minLength || value.length() > maxLength) {
            throw new ValidationException(
                    fieldName + " must be between " + minLength + " and " + maxLength + " characters"
            );
        }
    }

    public static void validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            throw new ValidationException("Invalid email format");
        }
    }
}
