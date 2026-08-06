# 03. Dependency Injection & Types of DI

> Dependency Injection is how Spring supplies collaborators to objects instead of letting objects construct them directly. Interviewers ask this topic to evaluate design judgment, especially why constructor injection is usually preferred.

## Core Concepts

### Constructor Injection
Dependencies are required through the constructor. This is preferred for mandatory collaborators because it supports immutability, clear contracts, and easy unit testing.

### Setter Injection
Dependencies are assigned after construction through setters. It is useful for optional dependencies or reconfiguration but makes objects temporarily incomplete.

### Field Injection
Spring writes directly into fields, usually with reflection. It is concise but discouraged because it hides dependencies, prevents `final` fields, complicates tests, and encourages circular references.

### Circular Dependencies
A circular dependency happens when bean A needs B and B needs A. Constructor cycles fail fast. Setter/field cycles may be resolved in some cases for singletons, but they usually indicate poor design.

## How It Works

For constructor injection, Spring selects a constructor, resolves each parameter from the bean factory, creates dependencies first, then instantiates the bean. For setter or field injection, Spring creates the object first and then populates properties. Autowiring uses type information, qualifiers, bean names, and primary markers to find candidates.

## Code Examples

```java
package com.example.payments;

import org.springframework.stereotype.Service;

interface GatewayClient {
    void charge(String accountId, int cents);
}

@Service
class PaymentService {
    private final GatewayClient gatewayClient;

    // In Spring 4.3+, a single constructor does not need @Autowired.
    PaymentService(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    void pay(String accountId, int cents) {
        if (cents <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        gatewayClient.charge(accountId, cents);
    }
}

@Service
class ReceiptService {
    private EmailClient emailClient;

    // Setter injection can be acceptable for optional dependencies.
    void setEmailClient(EmailClient emailClient) {
        this.emailClient = emailClient;
    }
}

interface EmailClient {
    void send(String body);
}
```

## Common Interview Questions

- **Q:** What is DI? **A:** Supplying an object's dependencies from outside rather than constructing them internally.
- **Q:** Which DI style is preferred? **A:** Constructor injection for required dependencies.
- **Q:** Why avoid field injection? **A:** It hides dependencies, blocks `final` fields, makes tests harder, and relies on reflection.
- **Q:** When is setter injection useful? **A:** For optional dependencies or when a framework requires a no-argument constructor.
- **Q:** How does Spring resolve constructor parameters? **A:** By type, then qualifiers, names, primary markers, and custom resolution rules.
- **Q:** What is a circular dependency? **A:** A dependency graph cycle, such as A needing B while B needs A.
- **Q:** How should circular dependencies be fixed? **A:** Refactor responsibilities, introduce a mediator, use events, or make one dependency lazy only as a last resort.
- **Q:** Can DI happen without Spring? **A:** Yes. Spring automates DI, but the pattern is framework-independent.

## Pitfalls & Best Practices

- Prefer one constructor with `final` fields for mandatory dependencies.
- Keep constructors free of heavy work; use lifecycle callbacks if needed.
- Do not inject too many dependencies; it may signal a class with too many responsibilities.
- Avoid `@Lazy` as a routine fix for circular design.
- In tests, instantiate constructor-injected classes directly with fakes or mocks.

## Related Topics

- IoC container
- Autowiring
- Bean lifecycle
- Stereotype annotations
- Testing annotations

