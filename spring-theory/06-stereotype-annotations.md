# 06. Stereotype Annotations

> Stereotype annotations mark application classes by role so Spring can discover them during component scanning. Interviewers ask this to see whether you know both the shared behavior and the semantic differences among `@Component`, `@Service`, `@Repository`, and controller annotations.

## Core Concepts

### `@Component`
Generic stereotype for any Spring-managed component. Other stereotypes are specialized forms of `@Component`.

### `@Service`
Marks service-layer classes that hold business logic. It currently behaves mostly like `@Component`, but communicates intent and may be targeted by tooling or aspects.

### `@Repository`
Marks persistence-layer components. It enables exception translation for supported persistence APIs, converting vendor-specific exceptions into Spring's data access exception hierarchy.

### `@Controller` and `@RestController`
`@Controller` handles MVC requests and usually returns view names. `@RestController` combines `@Controller` and `@ResponseBody`, so return values are serialized as HTTP responses.

### Component Scanning
`@ComponentScan` searches packages for classes annotated with stereotypes and registers bean definitions for them. Boot's `@SpringBootApplication` enables scanning from its package downward.

## How It Works

During startup, Spring reads class metadata without loading every class eagerly, finds stereotype annotations, applies include/exclude filters, derives bean names, and registers bean definitions. Later, those definitions are instantiated and wired like any other bean. Repository beans can be post-processed for exception translation.

## Code Examples

```java
package com.example.catalog;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Component
class SkuFormatter {
    String normalize(String sku) {
        return sku.trim().toUpperCase();
    }
}

@Repository
class ProductRepository {
    Product findBySku(String sku) {
        // Real code would call a database or Spring Data repository.
        return new Product(sku, "Keyboard");
    }
}

@Service
class ProductService {
    private final ProductRepository repository;
    private final SkuFormatter formatter;

    ProductService(ProductRepository repository, SkuFormatter formatter) {
        this.repository = repository;
        this.formatter = formatter;
    }

    Product getProduct(String sku) {
        return repository.findBySku(formatter.normalize(sku));
    }
}

@RestController
class ProductController {
    private final ProductService service;

    ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/products/{sku}")
    ResponseEntity<Product> find(@PathVariable String sku) {
        return ResponseEntity.ok(service.getProduct(sku));
    }
}

record Product(String sku, String name) {
}
```

## Common Interview Questions

- **Q:** Are stereotypes required for all beans? **A:** No. Beans can also come from `@Bean`, auto-configuration, or programmatic registration.
- **Q:** Is `@Service` technically different from `@Component`? **A:** Mostly semantic today, but it identifies service-layer intent.
- **Q:** What extra behavior does `@Repository` provide? **A:** Persistence exception translation when applicable.
- **Q:** Difference between `@Controller` and `@RestController`? **A:** `@RestController` adds `@ResponseBody` to serialize return values directly.
- **Q:** What package does Boot scan by default? **A:** The package of the main application class and its subpackages.
- **Q:** Can you customize scanning? **A:** Yes, with `@ComponentScan` base packages and filters.
- **Q:** How are bean names derived? **A:** Usually from the class name with a lower-cased first character, unless explicitly specified.

## Pitfalls & Best Practices

- Put the main application class in a top-level package.
- Do not annotate domain entities as Spring components.
- Use stereotypes to communicate architecture boundaries.
- Avoid broad component scans over unrelated packages.
- Prefer constructor injection in stereotype classes.

## Related Topics

- Bean registration
- Component scanning
- REST controllers
- Data repositories
- AOP and exception translation

