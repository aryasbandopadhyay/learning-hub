# 13. HTTP Status Codes for APIs

> Status codes are part of the API contract. Interviewers look for judgment: using success, redirect, client-error, and server-error codes consistently instead of always returning `200 OK` or leaking implementation exceptions.

## Core Concepts

### Status Code Families
- `2xx`: The request was successfully received, understood, and accepted.
- `3xx`: The client must use cached data or another URI.
- `4xx`: The client sent an invalid, unauthorized, forbidden, conflicting, or otherwise unprocessable request.
- `5xx`: The server failed to fulfill a valid request.

### Returning Status Codes
Use `ResponseEntity` for dynamic statuses and headers. Use `@ResponseStatus` for fixed statuses on handlers or exception types.

## How It Works

Spring MVC stores the selected status on the `HttpServletResponse`. `ResponseEntity` contributes status, headers, and body through return-value handlers. `@ResponseStatus` sets a fixed status when the handler returns or when an annotated exception is resolved. Global exception handlers often centralize `4xx` and `5xx` mapping.

## Code Examples

```java
package com.example.status;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/status-examples")
class StatusCodeController {

    @GetMapping("/200")
    ResponseEntity<String> ok200() {
        return ResponseEntity.ok("request succeeded");
    }

    @PostMapping("/201")
    ResponseEntity<String> created201() {
        return ResponseEntity.created(URI.create("/api/orders/42")).body("created");
    }

    @PostMapping("/202")
    ResponseEntity<String> accepted202() {
        return ResponseEntity.accepted().body("job accepted for async processing");
    }

    @DeleteMapping("/204")
    ResponseEntity<Void> noContent204() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/301")
    ResponseEntity<Void> movedPermanently301() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create("/api/new-resource"))
                .build();
    }

    @GetMapping("/302")
    ResponseEntity<Void> found302() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/login"))
                .build();
    }

    @GetMapping("/304")
    ResponseEntity<Void> notModified304() {
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                .eTag("\"orders-v1\"")
                .build();
    }

    @PostMapping("/400")
    ResponseEntity<String> badRequest400() {
        return ResponseEntity.badRequest().body("malformed JSON or invalid parameter");
    }

    @GetMapping("/401")
    ResponseEntity<String> unauthorized401() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                .body("authentication required");
    }

    @GetMapping("/403")
    ResponseEntity<String> forbidden403() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("not allowed");
    }

    @GetMapping("/404")
    ResponseEntity<String> notFound404() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("resource not found");
    }

    @PostMapping("/405")
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    void methodNotAllowed405() {
        // Usually produced automatically when the path exists but the HTTP method is unsupported.
    }

    @PutMapping("/409")
    ResponseEntity<String> conflict409() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("version conflict");
    }

    @PostMapping("/422")
    ResponseEntity<List<String>> unprocessable422() {
        return ResponseEntity.unprocessableEntity().body(List.of("business rule failed"));
    }

    @GetMapping("/429")
    ResponseEntity<String> tooManyRequests429() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, Long.toString(Duration.ofMinutes(1).toSeconds()))
                .body("rate limit exceeded");
    }

    @GetMapping("/500")
    ResponseEntity<String> internalServerError500() {
        return ResponseEntity.internalServerError().body("unexpected server error");
    }

    @GetMapping("/503")
    ResponseEntity<String> serviceUnavailable503() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HttpHeaders.RETRY_AFTER, "120")
                .body("dependency temporarily unavailable");
    }
}
```

## Common Interview Questions

- **Q:** When do you use `200 OK`? **A:** Successful reads or operations that return a representation.
- **Q:** When is `201 Created` appropriate? **A:** A new resource was created; include `Location` when possible.
- **Q:** What is `202 Accepted`? **A:** The request was accepted for asynchronous processing, but processing is not complete.
- **Q:** Why use `204 No Content`? **A:** The operation succeeded and there is no response body, common for delete or idempotent update.
- **Q:** What is the difference between `401` and `403`? **A:** `401` means authentication is missing/invalid; `403` means authenticated but not authorized.
- **Q:** When should an API return `409 Conflict`? **A:** State conflicts such as duplicate unique keys, stale versions, or optimistic-lock failures.
- **Q:** Is `422 Unprocessable Entity` the same as `400`? **A:** `400` is syntactic or generic invalid input; `422` is often used when syntax is valid but semantic validation fails.
- **Q:** When should `500` be used? **A:** For unexpected server bugs, not for predictable validation or authorization failures.
- **Q:** What does `503` communicate? **A:** Temporary unavailability, often with `Retry-After`.

## Pitfalls & Best Practices

- Do not return `200 OK` for errors.
- Do not expose stack traces in API responses.
- Include `Location` for newly created resources and redirects.
- Include `WWW-Authenticate` for `401` challenges.
- Prefer consistent error bodies, ideally `ProblemDetail`.
- Let Spring automatically produce `405` where possible.

## Related Topics

- 12 Building REST APIs
- 15 Exception Handling
- 19 Spring Security — Authentication
- 20 Spring Security — Authorization
