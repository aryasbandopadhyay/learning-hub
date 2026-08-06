# 01. Spring Framework & Spring Boot Overview

> Spring is a Java ecosystem for building loosely coupled, testable applications; Spring Boot is the opinionated layer that makes Spring production-ready with minimal setup. Interviewers ask this topic to check whether you understand both the core container ideas and the Boot conveniences that sit on top.

## Core Concepts

### What Spring Solves
Spring reduces boilerplate around object creation, wiring, transactions, web endpoints, validation, data access, messaging, scheduling, and configuration. Its central idea is: application classes should focus on business behavior while the framework manages infrastructure concerns.

### Major Spring Modules
- **Core Container:** IoC, dependency injection, bean lifecycle.
- **Spring AOP:** cross-cutting concerns through proxies.
- **Spring Web MVC/WebFlux:** HTTP request handling.
- **Spring Data:** repository abstraction over persistence technologies.
- **Spring Security:** authentication and authorization.
- **Spring Test:** testing support with cached application contexts.

### Spring Boot Value-Add
Spring Boot does not replace Spring; it configures Spring quickly:
- **Starters** collect compatible dependencies, such as `spring-boot-starter-web`.
- **Auto-configuration** creates beans based on classpath, properties, and existing beans.
- **Embedded servers** run apps as executable JARs.
- **Actuator** exposes health, metrics, and operational endpoints.
- **Externalized configuration** uses properties, YAML, environment variables, and profiles.

### `@SpringBootApplication`
This composed annotation combines:
- `@SpringBootConfiguration`
- `@EnableAutoConfiguration`
- `@ComponentScan`

## How It Works

On startup, `SpringApplication.run(...)` creates an `ApplicationContext`, loads configuration classes, scans components, applies auto-configurations, binds properties, creates beans, runs lifecycle callbacks, and finally starts infrastructure such as the embedded web server. Boot decides what to configure by reading metadata files under `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` and evaluating conditional annotations.

## Code Examples

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Combines configuration, component scanning, and auto-configuration.
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        // Creates the Spring ApplicationContext and starts the embedded server.
        SpringApplication.run(DemoApplication.class, args);
    }
}

@RestController
class HealthController {
    @GetMapping("/ping")
    String ping() {
        // Business code stays small because Spring handles routing and object wiring.
        return "pong";
    }
}
```

## Common Interview Questions

- **Q:** Is Spring Boot a replacement for Spring? **A:** No. Boot builds on Spring and provides defaults, starters, auto-configuration, and runtime packaging.
- **Q:** What is a starter? **A:** A dependency descriptor that brings a curated set of libraries for a capability, such as web, JPA, security, or testing.
- **Q:** What does `@SpringBootApplication` include? **A:** `@SpringBootConfiguration`, `@EnableAutoConfiguration`, and `@ComponentScan`.
- **Q:** What is auto-configuration? **A:** Boot conditionally creates framework beans based on classpath, properties, and missing user beans.
- **Q:** Why is Spring interview-relevant? **A:** It is heavily used in enterprise Java and tests understanding of dependency management, lifecycle, transactions, and web architecture.
- **Q:** How can you override Boot defaults? **A:** Define your own bean, set configuration properties, exclude an auto-configuration, or customize provided builders.
- **Q:** What is an embedded server? **A:** A server such as Tomcat, Jetty, or Undertow packaged inside the application process.

## Pitfalls & Best Practices

- Do not treat Boot magic as unknowable; learn the conditions behind auto-configuration.
- Keep the main class in a root package so component scanning finds subpackages.
- Prefer starters over hand-picking dependency versions.
- Override defaults with explicit beans rather than copying large framework configuration.
- Use Actuator in real systems, but expose endpoints carefully.

## Related Topics

- IoC container
- Bean registration
- Auto-configuration
- Externalized configuration
- Annotation catalog

