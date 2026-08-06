# 02. Inversion of Control (IoC) & the IoC Container

> Inversion of Control means objects do not manually create and manage their collaborators; the Spring container does it. Interviews focus on IoC because it explains why Spring applications are modular, testable, and configurable.

## Core Concepts

### IoC Mental Model
Without IoC, a class says `new PaymentGateway()`. With IoC, a class declares a dependency and Spring supplies an appropriate object. Control of object construction and wiring moves from application code to the container.

### Bean
A **bean** is an object managed by Spring. The container creates it, injects dependencies, applies lifecycle callbacks, and may wrap it in proxies.

### BeanFactory vs ApplicationContext
- **BeanFactory:** minimal container; lazy bean access; core bean creation API.
- **ApplicationContext:** richer container used in most apps; adds events, internationalization, environment, resource loading, and eager singleton creation.

### Container Responsibilities
- Read bean definitions from annotations, Java config, XML, or Boot auto-config.
- Resolve dependencies.
- Manage scopes and lifecycle.
- Publish events.
- Apply post-processors and proxies.

## How It Works

Spring builds a registry of `BeanDefinition` metadata, then refreshes the `ApplicationContext`. During refresh, it invokes factory post-processors, registers bean post-processors, creates non-lazy singleton beans, injects dependencies, calls initialization callbacks, and publishes context events. When the application stops, it destroys singleton beans in dependency-aware order.

## Code Examples

```java
package com.example.orders;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

interface TaxService {
    int taxFor(int amount);
}

class DefaultTaxService implements TaxService {
    public int taxFor(int amount) {
        return amount / 10;
    }
}

@Configuration
class AppConfig {
    @Bean
    TaxService taxService() {
        // Spring records this method as a bean definition and manages the return value.
        return new DefaultTaxService();
    }
}

class Demo {
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        // Application code asks the container for a managed object.
        TaxService taxService = context.getBean(TaxService.class);
        System.out.println(taxService.taxFor(500));
    }
}
```

## Common Interview Questions

- **Q:** What is IoC? **A:** A design principle where object creation and dependency wiring are delegated to an external container.
- **Q:** What is the IoC container in Spring? **A:** The `BeanFactory`/`ApplicationContext` infrastructure that manages beans.
- **Q:** Why is `ApplicationContext` preferred? **A:** It includes enterprise features beyond `BeanFactory`, such as events, resources, environment, and internationalization.
- **Q:** Are all Java objects Spring beans? **A:** No. Only objects registered with and managed by the container are beans.
- **Q:** What happens during context refresh? **A:** Bean definitions are processed, post-processors registered, singletons created, dependencies injected, and lifecycle callbacks invoked.
- **Q:** Is IoC the same as DI? **A:** DI is the most common implementation of IoC, but IoC is the broader principle.
- **Q:** Why does IoC improve testing? **A:** Dependencies are explicit and replaceable, making mocks and test configurations easier.

## Pitfalls & Best Practices

- Avoid calling `new` for services that need container features.
- Do not overuse `ApplicationContext.getBean()` in business code; prefer injection.
- Keep bean construction side effects minimal.
- Understand that proxies may mean the runtime bean class differs from your concrete class.
- Close manually created contexts to trigger destroy callbacks.

## Related Topics

- Dependency injection
- Bean definitions
- Bean lifecycle
- Application events
- Auto-configuration

