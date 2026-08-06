# 15. Exception Handling

> Exception handling turns failures into stable API contracts. Interviewers expect you to know local `@ExceptionHandler`, global advice, Spring MVC's default exception resolution, and modern `ProblemDetail` responses.

## Core Concepts

### Local Exception Handlers
An `@ExceptionHandler` method inside a controller handles exceptions thrown by that controller.

### Global Advice
`@ControllerAdvice` applies to MVC controllers. `@RestControllerAdvice` combines `@ControllerAdvice` and `@ResponseBody`, making it ideal for REST API error JSON.

### ResponseEntityExceptionHandler
Extending `ResponseEntityExceptionHandler` lets you override Spring MVC's built-in handling for validation errors, unreadable messages, missing parameters, and unsupported methods.

### ProblemDetail and RFC 7807
Spring Framework 6 supports `ProblemDetail`, a standard shape for machine-readable errors with fields such as `type`, `title`, `status`, `detail`, and `instance`.

## How It Works

If a controller throws an exception, `DispatcherServlet` delegates to `HandlerExceptionResolver` implementations. Spring first considers matching `@ExceptionHandler` methods, then framework resolvers, then Boot's fallback error handling. The chosen resolver writes the status and response body.

## Code Examples

```java
package com.example.errors;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
class ProductController {

    @GetMapping("/{id}")
    ProductDto find(@PathVariable long id) {
        if (id == 404) {
            throw new ProductNotFoundException(id);
        }
        return new ProductDto(id, "Keyboard");
    }
}

@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handleNotFound(ProductNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Product not found");
        problem.setType(URI.create("https://errors.example.com/product-not-found"));
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(DuplicateSkuException.class)
    ResponseEntity<ProblemDetail> handleDuplicateSku(DuplicateSkuException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Duplicate SKU");
        problem.setProperty("sku", ex.sku());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(ErrorResponseException.class)
    ProblemDetail handleSpringErrorResponse(ErrorResponseException ex) {
        return ex.getBody();
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal server error");
        problem.setDetail("An unexpected error occurred.");
        return problem;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            org.springframework.http.HttpHeaders headers,
            org.springframework.http.HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (left, right) -> left));

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation failed");
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }
}

record ProductDto(long id, String name) {}

class ProductNotFoundException extends RuntimeException {
    ProductNotFoundException(long id) {
        super("Product " + id + " was not found");
    }
}

class DuplicateSkuException extends RuntimeException {
    private final String sku;

    DuplicateSkuException(String sku) {
        super("SKU already exists: " + sku);
        this.sku = sku;
    }

    String sku() {
        return sku;
    }
}
```

## Common Interview Questions

- **Q:** What is the difference between `@ControllerAdvice` and `@RestControllerAdvice`? **A:** `@RestControllerAdvice` also applies `@ResponseBody`, so handler return values become response bodies.
- **Q:** When use local `@ExceptionHandler`? **A:** For controller-specific errors that should not affect the whole API.
- **Q:** Why centralize exception handling? **A:** To keep controllers clean and produce consistent status codes and error bodies.
- **Q:** What is `ProblemDetail`? **A:** Spring's RFC 7807 representation for structured API errors.
- **Q:** Should every exception become `500`? **A:** No. Map predictable domain and validation failures to appropriate `4xx` codes.
- **Q:** What does `ResponseEntityExceptionHandler` provide? **A:** Overridable handlers for common Spring MVC exceptions.
- **Q:** How should validation errors be returned? **A:** As structured field-level errors, typically `400` or sometimes `422`.
- **Q:** Should error responses expose stack traces? **A:** No. Log details server-side and return safe messages.

## Pitfalls & Best Practices

- Put specific exception handlers before broad fallback handlers.
- Avoid swallowing exceptions without logging or mapping them.
- Keep domain exceptions free of web framework dependencies where possible.
- Use stable error codes or `type` URIs for client handling.
- Never leak secrets, SQL, stack traces, or internal hostnames in error bodies.

## Related Topics

- 13 HTTP Status Codes for APIs
- 14 Request Validation
- 18 Transaction Management
