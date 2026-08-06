package com.example.crud.exception;

/**
 * ============================================================================================
 * ResourceNotFoundException — thrown when a requested entity does not exist.
 * ============================================================================================
 *
 * Extends {@link RuntimeException} (an "unchecked" exception) so callers are not forced to wrap
 * every service call in try/catch. It is translated into an HTTP 404 response centrally by
 * {@link GlobalExceptionHandler}, keeping the controller and service free of HTTP concerns.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id " + id);
    }
}
