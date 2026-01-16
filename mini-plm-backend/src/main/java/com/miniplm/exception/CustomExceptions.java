package com.miniplm.exception;

/**
 * Resource Not Found Exception
 * Used when a requested resource doesn't exist.
 */
class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s not found with id: %s", resourceName, identifier));
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Duplicate Resource Exception
 * Used when trying to create a resource that already exists.
 */
class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object value) {
        super(String.format("%s already exists with %s: %s", resourceName, fieldName, value));
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Unauthorized Exception
 * Used when user is not authenticated.
 */
class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Forbidden Exception
 * Used when user doesn't have required permissions.
 */
class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Invalid Operation Exception
 * Used for invalid business operations.
 */
class InvalidOperationException extends BusinessException {
    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Invalid State Exception
 * Used when entity is in invalid state for operation.
 */
class InvalidStateException extends BusinessException {
    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(String resourceName, String currentState, String requiredState) {
        super(String.format("%s is in %s state, but %s is required", resourceName, currentState, requiredState));
    }

    public InvalidStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
