# Spring Boot Web, Data & Security Theory Notes

| # | Topic | Summary |
|---|---|---|
| 01 | Spring Framework & Spring Boot Overview | Core motivation, Boot conventions, starters, embedded servers, and production-ready defaults. |
| 02 | Inversion of Control (IoC) & the IoC Container | How Spring creates, wires, configures, and manages application objects. |
| 03 | Dependency Injection & Types of DI | Constructor, setter, and field injection trade-offs and testability implications. |
| 04 | Beans: Definition, Registration & Autowiring | Bean metadata, component scanning, `@Bean` methods, qualifiers, and resolution rules. |
| 05 | Bean Scopes & Lifecycle | Singleton/prototype/web scopes, initialization, destruction, and lifecycle callbacks. |
| 06 | Stereotype Annotations | `@Component`, `@Service`, `@Repository`, and `@Controller` roles in layered design. |
| 07 | Configuration & Externalized Properties | Profiles, property sources, configuration binding, and environment-specific settings. |
| 08 | Auto-Configuration Deep Dive | Conditional configuration, classpath detection, starters, and overriding Boot defaults. |
| 09 | Spring AOP | Proxy-based cross-cutting concerns such as transactions, security, logging, and metrics. |
| 10 | The Spring Boot Annotation Catalog | Frequently used Boot annotations and where they fit in application code. |
| 11 | Spring MVC Architecture | DispatcherServlet front controller flow, handler resolution, adapters, views, and request lifecycle. |
| 12 | Building REST APIs | REST controllers, request mapping, payload binding, response shaping, content negotiation, and HATEOAS. |
| 13 | HTTP Status Codes for APIs | Practical status-code selection for API success, redirects, client errors, and server failures. |
| 14 | Request Validation | Jakarta Bean Validation, method validation, custom constraints, and validation error reporting. |
| 15 | Exception Handling | Local and global exception handlers, `ProblemDetail`, and consistent API error responses. |
| 16 | Data Access with Spring Data JPA | Entities, repositories, EntityManager, query methods, pagination, sorting, and Hibernate basics. |
| 17 | Database Connectivity & Configuration | DataSource setup, HikariCP pooling, JDBC vs JPA, JdbcTemplate, schema initialization, and H2. |
| 18 | Transaction Management | `@Transactional`, propagation, isolation, rollback rules, proxy limits, and locking strategies. |
| 19 | Spring Security — Authentication | Security filter chain, credential verification, password hashing, login styles, JWT, and AuthenticationManager. |
| 20 | Spring Security — Authorization | URL and method authorization, roles vs authorities, CORS, CSRF, and stateless resource-server security. |
