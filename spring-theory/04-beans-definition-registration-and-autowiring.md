# 04. Beans: Definition, Registration & Autowiring

> Beans are the objects Spring knows how to create, configure, wire, and manage. This topic matters in interviews because almost every Spring feature depends on correct bean registration and dependency resolution.

## Core Concepts

### Bean Definition vs Bean Instance
A `BeanDefinition` is metadata: class, scope, constructor arguments, factory method, qualifiers, and lifecycle settings. A bean instance is the actual object created from that metadata.

### Registration Approaches
- `@Component` and stereotypes discovered by component scanning.
- `@Bean` methods inside `@Configuration` classes.
- Boot auto-configuration creating conditional beans.
- Programmatic registration with registry APIs in advanced cases.

### Autowiring Rules
Spring usually autowires by type. If multiple beans match, it narrows candidates using:
- `@Qualifier`
- `@Primary`
- parameter or field name
- collection/map injection for multiple beans

### `@Configuration` Classes
Full `@Configuration` classes are proxied so calls between `@Bean` methods return the managed singleton instead of creating a new object manually.

## How It Works

Spring scans classpath packages for candidate annotations, parses configuration classes, registers bean definitions, and resolves dependencies at creation time. If a dependency has exactly one candidate, it is injected. If there are multiple candidates, Spring applies primary and qualifier metadata. If ambiguity remains, startup fails with a clear exception.

## Code Examples

```java
package com.example.notifications;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

interface MessageSender {
    void send(String message);
}

@Component
@Primary
class EmailSender implements MessageSender {
    public void send(String message) {
        System.out.println("email: " + message);
    }
}

@Component("smsSender")
class SmsSender implements MessageSender {
    public void send(String message) {
        System.out.println("sms: " + message);
    }
}

@Component
class NotificationService {
    private final MessageSender urgentSender;

    NotificationService(@Qualifier("smsSender") MessageSender urgentSender) {
        this.urgentSender = urgentSender;
    }
}

@Configuration
class NotificationConfig {
    @Bean
    AuditLogger auditLogger() {
        return new AuditLogger();
    }
}

class AuditLogger {
}
```

## Common Interview Questions

- **Q:** What is a Spring bean? **A:** An object managed by the Spring container.
- **Q:** What is the difference between `@Component` and `@Bean`? **A:** `@Component` marks a class for scanning; `@Bean` registers the object returned by a method.
- **Q:** When use `@Bean`? **A:** For third-party classes, conditional creation, or construction logic you do not want in the target class.
- **Q:** What does `@Primary` do? **A:** It marks the default candidate when multiple beans match by type.
- **Q:** What does `@Qualifier` do? **A:** It selects a specific bean among candidates.
- **Q:** Is autowiring by name or type? **A:** Primarily by type; name is used as a fallback/narrowing signal.
- **Q:** Why are `@Configuration` classes proxied? **A:** To preserve singleton semantics when one `@Bean` method calls another.
- **Q:** What happens if two beans match and none is primary or qualified? **A:** Spring fails startup with a `NoUniqueBeanDefinitionException`.

## Pitfalls & Best Practices

- Name beans intentionally when multiple implementations exist.
- Prefer constructor injection over field-level `@Autowired`.
- Use `@Qualifier` at injection points rather than relying on accidental parameter names.
- Avoid doing I/O in `@Bean` methods unless it is truly initialization.
- Keep configuration classes cohesive and small.

## Related Topics

- Dependency injection
- Stereotype annotations
- Auto-configuration
- Bean scopes
- Annotation catalog

