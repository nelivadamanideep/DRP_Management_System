package com.erpms.common.exception;

/**
 * Thrown when a lookup (by id, code, etc.) fails.
 * Maps to HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String entity, Object id) {
        return new ResourceNotFoundException(entity + " with identifier '" + id + "' was not found");
    }
}
