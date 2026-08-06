package com.example.crud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================================
 * GlobalExceptionHandler — centralizes error handling for ALL controllers.
 * ============================================================================================
 *
 * <h3>{@code @RestControllerAdvice}</h3>
 * A specialization combining {@code @ControllerAdvice} + {@code @ResponseBody}. It registers this
 * class as a cross-cutting interceptor: any exception thrown from any controller is offered to the
 * {@code @ExceptionHandler} methods below. This keeps controllers/services clean of try/catch and
 * guarantees a consistent error shape (Single Responsibility + DRY).
 *
 * <h3>{@code @ExceptionHandler}</h3>
 * Marks a method as the handler for a specific exception type. Spring picks the most specific match.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Maps our domain "not found" exception to HTTP 404. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        ApiError body = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Triggered when a {@code @Valid} request body fails Bean Validation. We collect each invalid
     * field and its message into a map so the client knows exactly what to fix, and return 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }
}
