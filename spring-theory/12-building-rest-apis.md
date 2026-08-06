# 12. Building REST APIs

> REST API interviews focus on mapping HTTP semantics to Spring MVC annotations. You should clearly explain how endpoints bind path, query, and body data, how `ResponseEntity` controls responses, and how content negotiation chooses representations.

## Core Concepts

### `@RestController`
`@RestController` is a convenience annotation combining `@Controller` and `@ResponseBody`. Every handler return value is written to the response body unless it is already a low-level response type.

### Request Mapping Annotations
`@RequestMapping` is the general mapping annotation. `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`, and `@DeleteMapping` are composed shortcuts that make HTTP intent explicit.

### Binding Inputs
- `@PathVariable` reads URI template values such as `/orders/{id}`.
- `@RequestParam` reads query parameters or form parameters.
- `@RequestBody` deserializes the request body, usually JSON, into a Java object.
- `@RequestHeader` and `@CookieValue` read headers and cookies.

### ResponseEntity
`ResponseEntity<T>` controls status, headers, and body. It is preferred when the status code or headers vary by outcome.

### Content Negotiation
Spring considers `Accept`, `Content-Type`, configured converters, and mapping attributes such as `produces` and `consumes` to decide how to read and write representations.

### HATEOAS Mention
HATEOAS adds links to responses so clients can discover valid transitions. Spring HATEOAS can build link-rich DTOs, but many JSON APIs use simpler resource DTOs.

## How It Works

For `@RequestBody`, Spring chooses an `HttpMessageConverter` based on `Content-Type`, deserializes the body, validates it if `@Valid` is present, and passes it to the controller. For the response, Spring uses the `Accept` header and supported converters to serialize the returned object. `ResponseEntity` wraps the body with explicit metadata.

## Code Examples

```java
package com.example.orders;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
class OrderController {

    private final OrderService service;

    OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    List<OrderDto> search(@RequestParam(defaultValue = "NEW") String status,
                          @RequestParam(defaultValue = "0") int page) {
        // Query parameters are optional by default when a default value is provided.
        return service.search(status, page);
    }

    @GetMapping("/{id}")
    ResponseEntity<OrderDto> findById(@PathVariable long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<OrderDto> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable long id) {
        service.delete(id);
    }
}

record CreateOrderRequest(@NotBlank String customerEmail, @Positive int quantity) {}
record OrderDto(long id, String customerEmail, int quantity, String status) {}
```

## Common Interview Questions

- **Q:** Why use `@RestController` instead of `@Controller`? **A:** It automatically serializes handler return values to the response body, which is the common REST behavior.
- **Q:** When should you use `@PathVariable` vs `@RequestParam`? **A:** Use path variables for resource identity and query parameters for filtering, sorting, pagination, or optional modifiers.
- **Q:** What does `@RequestBody` do? **A:** It asks Spring to deserialize the request payload into a Java object using a message converter.
- **Q:** Why return `ResponseEntity`? **A:** It gives explicit control over status codes, headers, and body.
- **Q:** What is content negotiation? **A:** The process of selecting a representation such as JSON or XML based on client headers and server capabilities.
- **Q:** What status should a successful POST return? **A:** Often `201 Created` with a `Location` header and representation of the created resource.
- **Q:** How do you version APIs? **A:** Common approaches include URI versioning, header/media-type versioning, or backward-compatible evolution.
- **Q:** What is HATEOAS? **A:** A REST maturity concept where responses include links that guide clients to related actions and resources.

## Pitfalls & Best Practices

- Use DTOs instead of exposing JPA entities directly.
- Model URLs around resources, not controller actions.
- Use correct HTTP methods and status codes.
- Validate inbound bodies and parameters.
- Keep pagination explicit for collection endpoints.
- Document media types, error shape, and authentication requirements.

## Related Topics

- 13 HTTP Status Codes for APIs
- 14 Request Validation
- 15 Exception Handling
- 16 Data Access with Spring Data JPA
