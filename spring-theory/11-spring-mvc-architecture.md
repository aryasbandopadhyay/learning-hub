# 11. Spring MVC Architecture

> Spring MVC is Spring Boot's servlet-stack web framework. Interviewers expect you to explain the front-controller pattern, how `DispatcherServlet` routes a request to controller methods, and where extension points such as `HandlerMapping`, `HandlerAdapter`, and `ViewResolver` fit.

## Core Concepts

### DispatcherServlet as Front Controller
`DispatcherServlet` receives all matching HTTP requests and coordinates the MVC pipeline. It does not contain business logic; it delegates to strategy interfaces for mapping, invocation, binding, conversion, exception handling, and view rendering.

### HandlerMapping
`HandlerMapping` finds the handler for a request. In annotation-based MVC, `RequestMappingHandlerMapping` maps URL, HTTP method, headers, params, and media types to `@Controller` or `@RestController` methods.

### HandlerAdapter
`HandlerAdapter` invokes the selected handler. `RequestMappingHandlerAdapter` understands method arguments such as `@PathVariable`, `@RequestBody`, `Principal`, `Model`, and return values such as `ResponseEntity`, `String`, view names, and objects.

### ViewResolver
For server-rendered MVC, a `ViewResolver` converts a logical view name like `"orders/detail"` into a concrete view technology such as Thymeleaf. REST endpoints usually bypass view resolution because `@ResponseBody` writes directly to the HTTP response.

### Controllers, Models, and Views
`@Controller` typically returns view names plus a `Model`. `@RestController` combines `@Controller` and `@ResponseBody`, so return values are serialized by `HttpMessageConverter`.

## How It Works

Request lifecycle diagram in words:

1. Client sends HTTP request to the embedded servlet container.
2. Servlet container routes the request to `DispatcherServlet`.
3. `DispatcherServlet` asks `HandlerMapping` for the best matching handler method.
4. Interceptors run `preHandle`.
5. `HandlerAdapter` invokes the controller method.
6. Spring binds request parameters, path variables, headers, cookies, and body content to method arguments.
7. Validation, type conversion, and message conversion happen as needed.
8. Controller returns a model/view, object, `ResponseEntity`, `ProblemDetail`, or another supported return type.
9. For REST, `HttpMessageConverter` serializes the body; for MVC views, `ViewResolver` resolves and renders the view.
10. Interceptors run `postHandle` and `afterCompletion`.
11. Exceptions are resolved by `HandlerExceptionResolver`, including `@ExceptionHandler` methods.
12. The servlet container sends the final HTTP response.

## Code Examples

```java
package com.example.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Controller
class PageController {

    @GetMapping("/orders/{id}")
    String orderPage(@PathVariable long id, Model model) {
        // HandlerMapping selects this method for GET /orders/{id}.
        // HandlerAdapter binds the {id} path segment to the long parameter.
        model.addAttribute("orderId", id);

        // ViewResolver maps "orders/detail" to a template such as orders/detail.html.
        return "orders/detail";
    }
}

@RestController
class HealthController {

    @GetMapping("/api/health")
    HealthResponse health() {
        // @RestController implies @ResponseBody.
        // The return object is written by an HttpMessageConverter, usually Jackson JSON.
        return new HealthResponse("UP");
    }
}

record HealthResponse(String status) {}
```

## Common Interview Questions

- **Q:** What problem does `DispatcherServlet` solve? **A:** It centralizes request dispatching so mapping, binding, validation, invocation, exception handling, and rendering are consistently applied.
- **Q:** Is `DispatcherServlet` a Spring bean? **A:** Yes. Boot auto-registers it and maps it to `/` by default for servlet web applications.
- **Q:** What is the difference between `HandlerMapping` and `HandlerAdapter`? **A:** Mapping finds the handler; adapter knows how to invoke it.
- **Q:** Why does REST usually not use `ViewResolver`? **A:** REST responses are written directly by message converters, not rendered as templates.
- **Q:** What are interceptors used for? **A:** Cross-cutting web concerns such as logging, locale changes, request timing, and pre-controller checks.
- **Q:** How are controller method arguments populated? **A:** Argument resolvers bind values from path variables, query parameters, headers, cookies, request bodies, session, security context, and model.
- **Q:** What component serializes Java objects to JSON? **A:** `HttpMessageConverter`, typically `MappingJackson2HttpMessageConverter`.
- **Q:** How are exceptions handled in MVC? **A:** Through `HandlerExceptionResolver`, local `@ExceptionHandler`, global `@ControllerAdvice`, and default Boot error handling.

## Pitfalls & Best Practices

- Prefer `@RestController` for APIs and `@Controller` for server-rendered views.
- Keep controllers thin; delegate business logic to services.
- Use specific request mappings to avoid ambiguous handler errors.
- Use interceptors for web-layer concerns, not security rules that belong in Spring Security.
- Do not return entity objects blindly from controllers; use DTOs for API boundaries.

## Related Topics

- 12 Building REST APIs
- 15 Exception Handling
- 19 Spring Security — Authentication
- 20 Spring Security — Authorization
