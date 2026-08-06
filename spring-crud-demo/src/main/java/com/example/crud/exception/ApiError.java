package com.example.crud.exception;

import java.time.Instant;
import java.util.Map;

/**
 * ============================================================================================
 * ApiError — a consistent JSON body returned for error responses.
 * ============================================================================================
 *
 * A {@code record} serialized by Jackson into e.g.:
 * <pre>
 * {
 *   "timestamp": "2024-01-01T12:00:00Z",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Product not found with id 99",
 *   "fieldErrors": { "name": "name is required" }
 * }
 * </pre>
 *
 * {@code fieldErrors} is only populated for validation failures; otherwise it is {@code null}.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors
) {
    public ApiError(int status, String error, String message, Map<String, String> fieldErrors) {
        this(Instant.now(), status, error, message, fieldErrors);
    }
}
