package com.erpms.common.exception;

/**
 * Thrown when a domain rule is violated (e.g. duplicate code,
 * non-idempotent operation, illegal state transition).
 * Maps to HTTP 409 (Conflict).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
