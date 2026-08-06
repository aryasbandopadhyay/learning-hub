# 09. Spring AOP

> Spring AOP applies cross-cutting behavior such as logging, metrics, security checks, and transactions without scattering that code through business methods. Interviewers ask this to verify proxy awareness and the limits of Spring's method-level AOP.

## Core Concepts

### Cross-Cutting Concerns
A concern is cross-cutting when many classes need similar logic: transactions, retries, audit logging, metrics, caching, or authorization.

### Aspect Terms
- **Aspect:** class containing cross-cutting logic.
- **Advice:** action taken at a join point.
- **Join point:** method execution in Spring AOP.
- **Pointcut:** expression selecting join points.
- **Weaving:** applying aspects; Spring does this through runtime proxies.

### Advice Types
- `@Before`
- `@After`
- `@AfterReturning`
- `@AfterThrowing`
- `@Around`

### JDK vs CGLIB Proxies
JDK proxies implement interfaces. CGLIB creates subclasses. Boot commonly uses class-based proxies when needed, but final classes/methods cannot be advised by subclass proxies.

## How It Works

Spring detects aspect beans and uses bean post-processors to wrap eligible beans in proxies. Callers receive the proxy from the container. When a matched method is invoked through the proxy, advice runs before, after, or around the target method. Self-invocation bypasses the proxy because `this.someMethod()` calls the target directly.

## Code Examples

```java
package com.example.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Aspect
@Component
class TimingAspect {
    @Around("execution(* com.example.aop..*Service.*(..))")
    Object timeServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedMicros = (System.nanoTime() - start) / 1_000;
            System.out.println(joinPoint.getSignature() + " took " + elapsedMicros + "us");
        }
    }
}

@Service
class InvoiceService {
    String createInvoice(String customerId) {
        // Call reaches this method through a proxy when invoked by another bean.
        return "INV-" + customerId;
    }
}
```

## Common Interview Questions

- **Q:** What is AOP? **A:** A programming model for applying cross-cutting behavior separately from business logic.
- **Q:** How does Spring AOP work? **A:** Primarily through runtime proxies around Spring beans.
- **Q:** What methods can Spring AOP advise? **A:** Method executions reached through Spring-managed proxies.
- **Q:** What is self-invocation? **A:** A method in the same class calling another advised method directly, bypassing the proxy.
- **Q:** JDK proxy vs CGLIB? **A:** JDK proxies implement interfaces; CGLIB subclasses concrete classes.
- **Q:** Why are final methods a problem? **A:** Subclass-based proxies cannot override final methods.
- **Q:** What advice type is most powerful? **A:** `@Around`, because it controls whether and when the target method proceeds.
- **Q:** Are Spring transactions AOP-based? **A:** Yes, declarative transactions are commonly implemented through proxies.

## Pitfalls & Best Practices

- Do not expect AOP to apply to objects created with `new`.
- Avoid self-invocation for transactional or advised methods.
- Keep pointcuts specific to avoid surprising matches.
- Be cautious with `@Around`; always call `proceed()` unless intentionally short-circuiting.
- Remember that proxy behavior can affect equality, class checks, and debugging.

## Related Topics

- Bean post-processors
- Transactions
- Stereotype annotations
- Proxy limitations
- Annotation catalog

