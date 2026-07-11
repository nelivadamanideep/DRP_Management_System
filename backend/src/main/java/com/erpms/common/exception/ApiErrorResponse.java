package com.erpms.common.exception;

import java.time.Instant;
import java.util.List;

/**
 * Standard error envelope returned by the {@link GlobalExceptionHandler}
 * for every failed HTTP request.
 *
 * <p>Keeping a single response shape lets the frontend render errors uniformly
 * and lets downstream systems (log aggregators, monitoring) rely on a stable
 * contract.
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> violations
) {

    public record FieldViolation(String field, String message) {}

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(Instant.now(), status, error, message, path, List.of());
    }

    public static ApiErrorResponse of(int status, String error, String message, String path,
                                      List<FieldViolation> violations) {
        return new ApiErrorResponse(Instant.now(), status, error, message, path, violations);
    }
}
