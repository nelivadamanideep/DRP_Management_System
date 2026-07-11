package com.erpms.common.exception;

/**
 * Thrown when the authenticated principal lacks permission for the current
 * operation and no Spring Security annotation-level check triggered.
 * Maps to HTTP 403.
 */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
