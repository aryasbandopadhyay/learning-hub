# 16. Data Access with Spring Data JPA

> Spring Data JPA is the standard interview topic for persistence in Boot applications. You should explain how entities map to tables, how Hibernate implements JPA, and how repositories remove boilerplate while still allowing custom queries.

## Core Concepts

### JPA and Hibernate
JPA is the specification for ORM in Jakarta EE. Hibernate is the most common JPA provider and implements entity state tracking, lazy loading, dirty checking, flush, and SQL generation.

### `@Entity`
An entity is a persistent domain object mapped to a database table. It needs an identifier, usually `@Id`, and should be designed carefully around equality, relationships, and lifecycle.

### EntityManager
`EntityManager` is the JPA API for persistence-context operations: `persist`, `find`, `merge`, `remove`, JPQL queries, flushing, and locking.

### JpaRepository
`JpaRepository<T, ID>` provides CRUD, pagination, sorting, batch operations, and query derivation. Spring creates a proxy implementation at runtime.

### Derived and Custom Queries
Methods such as `findByStatusAndCustomerEmail` are parsed into queries. Use `@Query` for JPQL or native SQL when derived names become unclear.

## How It Works

Inside a transaction, Hibernate keeps managed entities in the persistence context. Changes to managed objects are detected by dirty checking and flushed to SQL before commit or query execution. Repository methods delegate to JPA infrastructure, and Spring translates persistence exceptions into `DataAccessException` hierarchies.

## Code Examples

```java
package com.example.jpa;

import jakarta.persistence.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Entity
@Table(name = "orders")
class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.NEW;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Order() {
        // JPA requires a no-args constructor; protected prevents casual misuse.
    }

    Order(String customerEmail, BigDecimal total) {
        this.customerEmail = customerEmail;
        this.total = total;
    }

    Long id() {
        return id;
    }

    void markPaid() {
        this.status = OrderStatus.PAID;
    }
}

enum OrderStatus { NEW, PAID, CANCELLED }

interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Optional<Order> findByCustomerEmailAndStatus(String email, OrderStatus status);

    @Query("""
           select o
           from Order o
           where o.total >= :minimum
           order by o.createdAt desc
           """)
    Page<Order> findLargeOrders(@Param("minimum") BigDecimal minimum, Pageable pageable);
}

@Service
class OrderApplicationService {
    private final OrderRepository repository;
    private final EntityManager entityManager;

    OrderApplicationService(OrderRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Transactional
    Long create(String email, BigDecimal total) {
        Order saved = repository.save(new Order(email, total));
        return saved.id();
    }

    @Transactional(readOnly = true)
    Page<Order> listPaid(Pageable pageable) {
        return repository.findByStatus(OrderStatus.PAID, pageable);
    }

    @Transactional
    void markPaid(long id) {
        Order order = entityManager.find(Order.class, id);
        order.markPaid(); // Hibernate dirty checking updates the row on flush.
    }
}
```

## Common Interview Questions

- **Q:** What is the difference between JPA and Hibernate? **A:** JPA is the specification; Hibernate is an implementation.
- **Q:** What does `JpaRepository` provide? **A:** CRUD, pagination, sorting, flushing, batch methods, and generated query implementations.
- **Q:** What is the persistence context? **A:** The first-level cache of managed entities associated with an `EntityManager`.
- **Q:** What is dirty checking? **A:** Hibernate detects changes to managed entities and writes SQL during flush.
- **Q:** When use `@Query`? **A:** When derived method names are too complex or a precise JPQL/native query is needed.
- **Q:** What is lazy loading? **A:** Associated data is fetched only when accessed, usually through a proxy.
- **Q:** What causes `LazyInitializationException`? **A:** Accessing an unfetched lazy association after the persistence context is closed.
- **Q:** How do pagination and sorting work? **A:** Repository methods accept `Pageable` or `Sort`; Spring Data applies limit/offset and order clauses.

## Pitfalls & Best Practices

- Do not expose entities directly through REST APIs.
- Avoid large derived method names; prefer `@Query` or specifications.
- Be cautious with bidirectional relationships and JSON serialization.
- Use `@Transactional(readOnly = true)` for read service methods.
- Avoid Open Session in View as a substitute for proper fetch planning.
- Use database constraints as the final guard for uniqueness and integrity.

## Related Topics

- 17 Database Connectivity & Configuration
- 18 Transaction Management
- 12 Building REST APIs
