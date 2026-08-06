# 07. Configuration & Externalized Properties

> Externalized configuration lets the same application artifact run in different environments without code changes. Interviewers ask this to test practical Boot knowledge: properties, profiles, binding, and precedence.

## Core Concepts

### Configuration Files
Spring Boot reads `application.properties` or `application.yml` from standard locations. YAML is hierarchical and often easier for structured settings.

### `@Value`
Injects a single property or expression. It is useful for simple values but becomes noisy for groups of related properties.

### `@ConfigurationProperties`
Binds a group of properties to a typed object. This is preferred for structured configuration because it supports validation, metadata, and immutable-style records/classes.

### Profiles
`@Profile` and profile-specific files such as `application-prod.yml` activate environment-specific beans and settings.

### Environment and Precedence
Spring's `Environment` abstracts property sources. Command-line arguments and environment variables can override packaged defaults.

## How It Works

Boot builds a chain of property sources, resolves active profiles, loads profile-specific configuration, and binds values through the Binder. Relaxed binding maps names such as `payment.timeout-ms`, `PAYMENT_TIMEOUT_MS`, and `payment.timeoutMs` to the same Java property. Later property sources with higher priority override lower-priority values.

## Code Examples

```java
package com.example.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "payment")
record PaymentProperties(
        @NotBlank String provider,
        @Min(100) int timeoutMs,
        boolean sandbox
) {
}

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
class PaymentConfig {
}

@Component
class PaymentClient {
    private final PaymentProperties properties;

    PaymentClient(PaymentProperties properties) {
        this.properties = properties;
    }
}

@Component
@Profile("dev")
class DevOnlyDataLoader {
    DevOnlyDataLoader(@Value("${app.seed-count:10}") int seedCount) {
        // Default value 10 is used if app.seed-count is missing.
    }
}
```

## Common Interview Questions

- **Q:** Why externalize configuration? **A:** To deploy the same build artifact across environments with different settings.
- **Q:** `@Value` vs `@ConfigurationProperties`? **A:** `@Value` is for individual values; `@ConfigurationProperties` is for grouped, typed, validated configuration.
- **Q:** What is relaxed binding? **A:** Boot maps different property naming styles to Java fields.
- **Q:** What are profiles? **A:** Named environment conditions that activate specific properties or beans.
- **Q:** How do environment variables map to properties? **A:** Uppercase underscore names such as `SERVER_PORT` map to `server.port`.
- **Q:** Which property wins? **A:** The higher-precedence property source, such as command-line args over packaged config.
- **Q:** Should secrets be committed in `application.yml`? **A:** No. Use environment variables, secret stores, or deployment platform mechanisms.
- **Q:** What is `Environment`? **A:** Spring's abstraction for active profiles and property lookup.

## Pitfalls & Best Practices

- Prefer typed configuration over scattered `@Value` fields.
- Validate required configuration early.
- Keep secrets out of source control.
- Use profiles for coarse environment differences, not every feature flag.
- Document important property names and defaults.

## Related Topics

- Auto-configuration
- Profiles
- Bean registration
- Validation
- Spring Boot annotation catalog

