# 10. The Spring Boot Annotation Catalog

> Spring interviews often become annotation interviews; this catalog groups the most common annotations by purpose and explains what each one signals. The goal is not memorization, but knowing which part of the framework each annotation activates.

## Core Concepts

### Core and Boot
- `@SpringBootApplication`: main Boot entry point.
- `@Configuration`: declares configuration class.
- `@Bean`: registers a bean from a method.
- `@ComponentScan`: selects packages to scan.
- `@Autowired`: asks Spring to inject a dependency.
- `@Qualifier`: chooses a specific bean.
- `@Primary`: marks the preferred bean.
- `@Profile`: activates beans for selected profiles.

### Web
- `@RestController`: controller whose methods write response bodies.
- `@Controller`: MVC controller, often returning views.
- `@RequestMapping`: generic request mapping.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: HTTP method shortcuts.
- `@PathVariable`: binds a URI template variable.
- `@RequestParam`: binds a query/form parameter.
- `@RequestBody`: deserializes the request body.
- `@ResponseStatus`: sets response status.

### Data
- `@Repository`: persistence component and exception translation marker.
- `@Transactional`: wraps method execution in a transaction.
- `@Entity`: JPA-managed persistent class.
- `@Id`: primary key field.

### Security
- `@EnableWebSecurity`: enables web security configuration.
- `@PreAuthorize`: method-level authorization check.
- `@Secured`: role-based method security annotation.

### Testing
- `@SpringBootTest`: full application context test.
- `@WebMvcTest`: MVC slice test.
- `@DataJpaTest`: JPA slice test.
- `@MockBean`: replaces a bean with a Mockito mock in the test context.

## How It Works

Most annotations are metadata. Spring reads them during configuration parsing, component scanning, bean creation, request mapping registration, transaction proxy creation, or test context setup. Some are composed annotations, meaning they bundle other annotations. For example, `@RestController` includes `@Controller` and `@ResponseBody`.

## Code Examples

```java
package com.example.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication // bootstraps component scanning and auto-configuration
class CatalogApplication {
}

@Configuration // declares bean factory methods
class ClockConfig {
    @Bean
    @Profile("prod")
    java.time.Clock systemClock() {
        return java.time.Clock.systemUTC();
    }
}

@Entity // JPA maps this class to a table
class Book {
    @Id
    Long id;
    String title;
}

@Repository
interface BookRepository {
    Book findById(Long id);
}

@Service
class BookService {
    private final BookRepository repository;

    BookService(BookRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    Book find(Long id) {
        return repository.findById(id);
    }
}

@RestController
class BookController {
    private final BookService service;

    BookController(BookService service) {
        this.service = service;
    }

    @GetMapping("/books/{id}")
    Book find(@PathVariable Long id) {
        return service.find(id);
    }
}
```

## Common Interview Questions

- **Q:** What is a composed annotation? **A:** An annotation meta-annotated with other annotations, such as `@SpringBootApplication`.
- **Q:** Is `@Autowired` required on constructors? **A:** Not when a bean has a single constructor.
- **Q:** What is the difference between `@Controller` and `@RestController`? **A:** `@RestController` serializes return values as response bodies.
- **Q:** What does `@Transactional` usually rely on? **A:** A Spring AOP proxy around the bean.
- **Q:** What does `@Repository` add? **A:** It marks persistence components and enables exception translation where supported.
- **Q:** When use `@SpringBootTest`? **A:** When you need a broad integration test with the full application context.
- **Q:** What is a slice test? **A:** A test loading only part of the context, such as MVC or JPA infrastructure.
- **Q:** What is `@PreAuthorize`? **A:** A method security annotation that evaluates an expression before method execution.

## Pitfalls & Best Practices

- Do not add annotations without knowing which subsystem processes them.
- Prefer specialized test annotations over `@SpringBootTest` for fast focused tests.
- Remember that security and transactions often require proxy-based method calls.
- Avoid field injection even though `@Autowired` supports it.
- Use annotations to express architecture, not to hide business logic.

## Related Topics

- Spring Boot overview
- Bean registration
- Stereotype annotations
- AOP
- Configuration properties

