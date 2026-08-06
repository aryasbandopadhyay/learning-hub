# 05. Bean Scopes & Lifecycle

> Bean scope controls how many instances Spring creates, while lifecycle callbacks control what happens as beans start and stop. Interviews use this topic to test whether you understand singleton defaults, web scopes, and extension points.

## Core Concepts

### Common Scopes
- **singleton:** one bean instance per Spring container; default.
- **prototype:** a new instance each time requested from the container.
- **request:** one instance per HTTP request.
- **session:** one instance per HTTP session.
- **application:** one instance per servlet context.

### Lifecycle Callbacks
- `@PostConstruct`: called after dependency injection.
- `@PreDestroy`: called before singleton destruction.
- `InitializingBean` / `DisposableBean`: Spring-specific lifecycle interfaces.
- custom `initMethod` / `destroyMethod` on `@Bean`.

### BeanPostProcessor
A `BeanPostProcessor` can inspect or wrap beans before and after initialization. AOP proxies are created through this extension mechanism.

## How It Works

For singletons, Spring creates instances during context startup by default, injects dependencies, runs aware callbacks, invokes post-processors, calls initialization methods, and stores the finished bean in the singleton cache. During shutdown, it calls destroy callbacks for singleton beans. Prototype beans are created and initialized by Spring, but Spring does not manage their full destruction lifecycle.

## Code Examples

```java
package com.example.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
class CacheWarmupService {
    @PostConstruct
    void loadReferenceData() {
        // Dependencies are already injected here.
        System.out.println("warming cache");
    }

    @PreDestroy
    void flushMetrics() {
        // Called for singleton beans during graceful shutdown.
        System.out.println("flushing metrics");
    }
}

@Configuration
class ScopeConfig {
    @Bean
    @Scope("prototype")
    JobContext jobContext() {
        // A new instance is returned for each container lookup.
        return new JobContext();
    }
}

class JobContext {
}

@Component
class LoggingBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // Frameworks use this hook to wrap beans with proxies.
        return bean;
    }
}
```

## Common Interview Questions

- **Q:** What is the default bean scope? **A:** Singleton.
- **Q:** Does singleton mean JVM-wide singleton? **A:** No. It means one instance per Spring container.
- **Q:** What is prototype scope? **A:** Spring creates a new instance whenever the bean is requested from the container.
- **Q:** Does Spring call destroy callbacks on prototype beans? **A:** Not automatically after handing them out.
- **Q:** What is `@PostConstruct`? **A:** A callback invoked after dependency injection and before the bean is ready for use.
- **Q:** What is `BeanPostProcessor` used for? **A:** Customizing or wrapping beans around initialization, including proxy creation.
- **Q:** How do web scopes work in singleton beans? **A:** Use scoped proxies or object providers to access request/session-scoped beans safely.
- **Q:** When is `@PreDestroy` called? **A:** During context shutdown for managed singleton beans.

## Pitfalls & Best Practices

- Do not store per-request mutable state in singleton beans.
- Be careful injecting prototype beans into singletons; the prototype is resolved once unless you use a provider.
- Prefer `@PostConstruct` over putting initialization logic in constructors when dependencies are needed.
- Keep `BeanPostProcessor` implementations generic and safe; they affect many beans.
- Ensure graceful shutdown is enabled when cleanup matters.

## Related Topics

- IoC container
- Dependency injection
- AOP proxies
- Web scopes
- ApplicationContext lifecycle

