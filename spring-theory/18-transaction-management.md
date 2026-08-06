# 18. Transaction Management

> Transaction management is a favorite interview area because it combines annotations, proxies, database behavior, and failure semantics. You should be able to explain `@Transactional`, propagation, isolation, rollback defaults, and common proxy pitfalls.

## Core Concepts

### `@Transactional`
`@Transactional` defines transaction boundaries around a method or class. Spring usually applies it through AOP proxies around service-layer methods.

### Propagation
Propagation controls how a method participates in an existing transaction. Common values: `REQUIRED`, `REQUIRES_NEW`, `MANDATORY`, `SUPPORTS`, `NOT_SUPPORTED`, `NEVER`, and `NESTED`.

### Isolation
Isolation controls visibility of concurrent transaction changes. Standard levels include `READ_UNCOMMITTED`, `READ_COMMITTED`, `REPEATABLE_READ`, and `SERIALIZABLE`.

### Rollback Rules
By default, Spring rolls back on unchecked exceptions (`RuntimeException` and `Error`) and commits on checked exceptions unless configured with `rollbackFor`.

### Locking
Optimistic locking uses a version column and detects conflicts. Pessimistic locking uses database locks to prevent concurrent modification.

## How It Works

Spring creates a proxy for a transactional bean. External method calls pass through the proxy, which opens or joins a transaction using a `PlatformTransactionManager`, invokes the method, then commits or rolls back. Self-invocation, private methods, and final methods can bypass proxy advice depending on proxy type.

## Code Examples

```java
package com.example.transactions;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Entity
class Account {
    @Id
    private Long id;

    @Version
    private long version; // Enables optimistic locking.

    @Column(nullable = false)
    private BigDecimal balance;

    void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }
}

interface AccountRepository extends JpaRepository<Account, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    java.util.Optional<Account> findWithLockById(Long id);
}

@Service
class TransferService {
    private final AccountRepository repository;
    private final AuditService auditService;

    TransferService(AccountRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void transfer(long fromId, long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();

        from.withdraw(amount);
        to.deposit(amount);

        // Runs in its own transaction; it can commit even if the transfer rolls back.
        auditService.record("transfer requested");
    }

    @Transactional(rollbackFor = PaymentGatewayException.class)
    public void chargeAndTransfer() throws PaymentGatewayException {
        // Checked exception rollback must be declared explicitly.
        throw new PaymentGatewayException();
    }
}

@Service
class AuditService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String message) {
        // Persist an audit record in an independent transaction.
    }
}

class PaymentGatewayException extends Exception {}
```

## Common Interview Questions

- **Q:** Where should `@Transactional` usually be placed? **A:** On service-layer methods that represent business use cases.
- **Q:** What is the default propagation? **A:** `REQUIRED`, meaning join an existing transaction or create one.
- **Q:** What is `REQUIRES_NEW`? **A:** It suspends the current transaction and starts a separate one.
- **Q:** What exceptions trigger rollback by default? **A:** Runtime exceptions and errors.
- **Q:** How do checked exceptions roll back? **A:** Configure `rollbackFor` or throw an unchecked domain exception.
- **Q:** What is the self-invocation pitfall? **A:** A method in the same class calling another transactional method bypasses the proxy, so advice may not run.
- **Q:** What is optimistic locking? **A:** A version check detects concurrent updates and fails one transaction.
- **Q:** What is pessimistic locking? **A:** The database locks rows to block conflicting concurrent access.
- **Q:** Does `readOnly = true` guarantee no writes? **A:** It is a hint/optimization; the database and provider behavior can vary.

## Pitfalls & Best Practices

- Keep transactions short and avoid remote calls inside them when possible.
- Do not rely on `@Transactional` on private methods.
- Understand rollback defaults before using checked exceptions.
- Avoid self-invocation; move methods to another bean or call through the proxy deliberately.
- Use optimistic locking for common web concurrency conflicts.
- Use pessimistic locking sparingly because it can reduce throughput.

## Related Topics

- 16 Data Access with Spring Data JPA
- 17 Database Connectivity & Configuration
- 15 Exception Handling
