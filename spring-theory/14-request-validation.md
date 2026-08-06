# 14. Request Validation

> Request validation protects service boundaries and keeps controllers from processing invalid data. In interviews, you should distinguish object validation with `@Valid`, method validation with `@Validated`, standard Jakarta constraints, and custom constraints.

## Core Concepts

### Bean Validation
Spring Boot 3 uses Jakarta Bean Validation packages (`jakarta.validation.*`). Common constraints include `@NotNull`, `@NotBlank`, `@Size`, `@Min`, `@Max`, `@Positive`, `@Email`, `@Pattern`, `@Past`, and `@Future`.

### `@Valid`
`@Valid` triggers validation for request bodies, nested fields, model attributes, and constructor-bound objects. For nested object graphs, put `@Valid` on the nested property too.

### `@Validated`
`@Validated` is Spring's variant that supports validation groups and method-level validation. Put it on a controller or service class to validate `@PathVariable` and `@RequestParam` constraints.

### Custom Validators
Create a constraint annotation and a `ConstraintValidator` when validation requires reusable domain-specific rules.

## How It Works

Spring deserializes the request body first, then invokes the Bean Validation provider, typically Hibernate Validator. If validation fails for `@RequestBody`, Spring raises `MethodArgumentNotValidException`. For method parameters, Spring can raise `HandlerMethodValidationException` or `ConstraintViolationException` depending on the validation path and Spring version. Exception handlers convert these failures into client-friendly error responses.

## Code Examples

```java
package com.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.*;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/customers")
class CustomerController {

    @PostMapping
    CustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        // If validation fails, this method is not called.
        return new CustomerResponse(100L, request.email());
    }

    @GetMapping("/{id}")
    CustomerResponse find(@PathVariable @Positive long id,
                          @RequestParam(defaultValue = "false") boolean includeOrders,
                          @RequestParam(required = false) @Size(max = 20) String tag) {
        return new CustomerResponse(id, "customer@example.com");
    }
}

record CreateCustomerRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @Min(18) int age,
        @Valid AddressRequest address,
        List<@NotBlank String> tags,
        @StrongCustomerCode String customerCode
) {}

record AddressRequest(@NotBlank String city, @NotBlank String postalCode) {}
record CustomerResponse(long id, String email) {}

@Documented
@Constraint(validatedBy = StrongCustomerCodeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@interface StrongCustomerCode {
    String message() default "customer code must start with CUST- and contain digits";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class StrongCustomerCodeValidator implements ConstraintValidator<StrongCustomerCode, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.matches("CUST-\\d{4,}");
    }
}
```

## Common Interview Questions

- **Q:** What dependency enables validation in Boot? **A:** `spring-boot-starter-validation`, which brings Jakarta Bean Validation and Hibernate Validator.
- **Q:** What is the difference between `@Valid` and `@Validated`? **A:** `@Valid` is the standard trigger; `@Validated` adds Spring support for groups and method validation.
- **Q:** How do you validate path variables? **A:** Put constraints on parameters and annotate the controller class with `@Validated`.
- **Q:** How do you validate nested objects? **A:** Put `@Valid` on the nested field or collection element.
- **Q:** What exception is common for invalid request bodies? **A:** `MethodArgumentNotValidException`.
- **Q:** Should validation be only in the controller? **A:** No. Controllers validate API shape; services should still enforce business invariants.
- **Q:** When would you write a custom validator? **A:** For reusable, declarative domain rules not covered by built-in constraints.
- **Q:** What are validation groups? **A:** Named groups that apply different constraints for use cases such as create vs update.

## Pitfalls & Best Practices

- Use wrapper types when `null` has meaning; primitives cannot represent missing values.
- Keep DTO validation separate from persistence constraints.
- Return structured field errors, not a single vague message.
- Avoid putting database lookups in simple validators unless carefully designed.
- Validate both request bodies and path/query parameters.
- Prefer immutable request records for simple DTOs.

## Related Topics

- 12 Building REST APIs
- 15 Exception Handling
- 16 Data Access with Spring Data JPA
