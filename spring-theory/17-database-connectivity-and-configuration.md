# 17. Database Connectivity & Configuration

> Database configuration questions test whether you understand Boot's `DataSource` auto-configuration, connection pooling, and the difference between low-level JDBC and ORM-based JPA access.

## Core Concepts

### DataSource
`DataSource` is the JDBC connection factory. Spring Boot creates one when it finds database properties and a JDBC driver.

### HikariCP
HikariCP is Boot's default connection pool. It keeps reusable database connections so each request does not pay the cost of opening a new TCP/database session.

### JDBC vs JPA
JDBC is SQL-first and explicit. JPA is entity-first and uses ORM. `JdbcTemplate` is excellent for simple SQL, reports, and performance-sensitive queries; JPA is productive for aggregate persistence and relationships.

### Schema Initialization
Boot can run `schema.sql` and `data.sql` for basic initialization. Migration tools such as Flyway or Liquibase are preferred for versioned production schema changes.

### H2
H2 is an in-memory database often used for local demos and tests. It is convenient but not identical to production databases.

## How It Works

Boot checks the classpath and properties under `spring.datasource.*`. If a pool implementation is available, Boot creates a pooled `DataSource`, then creates `JdbcTemplate`, transaction managers, and JPA infrastructure as applicable. Pool settings control maximum connections, idle timeout, connection lifetime, and validation behavior.

## Code Examples

```java
package com.example.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

// application.yml example:
// spring:
//   datasource:
//     url: jdbc:postgresql://localhost:5432/shop
//     username: shop_app
//     password: change-me-outside-source-control
//     hikari:
//       maximum-pool-size: 20
//       minimum-idle: 5
//   jpa:
//     hibernate:
//       ddl-auto: validate
//   sql:
//     init:
//       mode: never

@Configuration
class ReportingDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("app.reporting-datasource")
    DataSource reportingDataSource() {
        // Define an extra DataSource only when the application truly needs multiple databases.
        return DataSourceBuilder.create().build();
    }
}

@Repository
class SalesReportRepository {
    private final JdbcTemplate jdbcTemplate;

    SalesReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    List<SalesRow> topProducts() {
        return jdbcTemplate.query("""
                select product_name, sum(total) as revenue
                from order_lines
                group by product_name
                order by revenue desc
                limit 10
                """,
                (rs, rowNum) -> new SalesRow(
                        rs.getString("product_name"),
                        rs.getBigDecimal("revenue")));
    }
}

record SalesRow(String productName, BigDecimal revenue) {}
```

## Common Interview Questions

- **Q:** What is a `DataSource`? **A:** A factory for JDBC connections, usually backed by a connection pool.
- **Q:** Why use connection pooling? **A:** Opening database connections is expensive; pooling reuses them safely and limits concurrency.
- **Q:** What pool does Boot use by default? **A:** HikariCP when it is on the classpath.
- **Q:** When choose `JdbcTemplate` over JPA? **A:** For explicit SQL, reporting, bulk operations, or queries that do not fit an entity model.
- **Q:** What is `ddl-auto`? **A:** A Hibernate setting controlling schema generation/validation; use `validate` or migrations in production.
- **Q:** How should secrets be provided? **A:** Through environment variables, secret stores, or deployment configuration, not committed files.
- **Q:** What is H2 useful for? **A:** Fast local demos and tests, while remembering dialect differences from production databases.
- **Q:** How do you configure multiple databases? **A:** Define multiple `DataSource` beans and qualify templates, entity managers, and transaction managers.

## Pitfalls & Best Practices

- Do not commit real database passwords.
- Size pools based on database capacity, not only application thread count.
- Prefer Flyway or Liquibase for production schema evolution.
- Keep test database behavior close to production when SQL dialect matters.
- Monitor pool exhaustion, slow queries, and connection leaks.
- Avoid `ddl-auto=create` or `update` in production.

## Related Topics

- 16 Data Access with Spring Data JPA
- 18 Transaction Management
