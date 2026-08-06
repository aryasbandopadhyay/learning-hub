# Learning Guide: Spring Boot Annotations, IoC & DI

This is a concise reference for the concepts demonstrated in this project. Read it alongside the
commented source files.

---

## 1. Inversion of Control (IoC) — the big idea

Normally *your* code creates the objects it needs (`new ProductRepository()`). With **IoC**, you
**hand that responsibility to Spring**. At startup, Spring builds an **application context** (the
"IoC container"), creates the objects your app needs (called **beans**), and wires them together.

You no longer call `new` for your services/repositories/controllers — Spring does, and gives them
to whoever needs them. Control over object creation is *inverted* from your code to the framework.

## 2. Dependency Injection (DI) — how IoC delivers collaborators

**DI** is the mechanism: instead of a class fetching its dependencies, the container **injects**
them. Three styles exist; this project uses the recommended one:

| Style                    | Example                                             | Recommended? |
| ------------------------ | --------------------------------------------------- | ------------ |
| **Constructor injection**| `public Svc(Repo r) { this.r = r; }`                | ✅ Yes        |
| Setter injection         | `@Autowired public void setRepo(Repo r) {...}`      | Sometimes    |
| Field injection          | `@Autowired private Repo r;`                         | ❌ Avoid      |

**Why constructor injection?** Dependencies become `final` (immutable, never null), the class is
honest about what it needs, and it is trivially unit-testable with `new Svc(mock)` — no Spring
required. Since Spring 4.3, a single-constructor bean needs **no `@Autowired`** annotation.

See it in: `service/impl/ProductServiceImpl.java`, `controller/ProductController.java`.

---

## 3. Bean-defining ("stereotype") annotations

Spring's **component scanning** finds classes annotated with these and registers one bean each.
They are functionally similar; the specific name documents the role (and enables extra behavior).

| Annotation        | Layer / role                    | In this project              |
| ----------------- | ------------------------------- | ---------------------------- |
| `@Component`      | Generic bean                    | `ProductMapper`              |
| `@Service`        | Business-logic bean             | `ProductServiceImpl`         |
| `@Repository`     | Persistence bean (+ exception translation) | `ProductRepository` |
| `@Controller`     | Web MVC controller (views)      | —                            |
| `@RestController` | `@Controller` + `@ResponseBody` | `ProductController`          |
| `@Configuration`  | Declares `@Bean` methods        | `DataInitializer`            |

By default every bean is a **singleton** — the container creates exactly one shared instance.

---

## 4. `@Bean` vs `@Component`

- `@Component` (and friends): Spring instantiates the class **for you** via component scanning.
- `@Bean`: **you** write a factory method (inside a `@Configuration` class) that constructs and
  returns the object. Use it when you need custom construction, or for types you don't own.

`@Bean` method **parameters are injected** from the container — same rules as a constructor.
See `config/DataInitializer.java`, where a `CommandLineRunner` bean is built and a
`ProductRepository` is injected into the method.

---

## 5. The entry point: `@SpringBootApplication`

A meta-annotation combining three:

| Combined annotation          | What it does                                              |
| ---------------------------- | -------------------------------------------------------- |
| `@Configuration`             | The class can declare beans                               |
| `@EnableAutoConfiguration`   | Auto-configures beans from the classpath (H2, JPA, MVC…)  |
| `@ComponentScan`             | Scans this package + sub-packages for stereotypes         |

`SpringApplication.run(...)` boots the container, starts the embedded Tomcat, and wires everything.
See `CrudDemoApplication.java`.

---

## 6. Persistence annotations (JPA / Hibernate)

| Annotation                     | Meaning                                            |
| ------------------------------ | -------------------------------------------------- |
| `@Entity`                      | This class maps to a DB table                      |
| `@Table(name=...)`             | (optional) explicit table name                     |
| `@Id`                          | The primary-key field                              |
| `@GeneratedValue(IDENTITY)`    | DB auto-generates the key on insert                |
| `@Column(...)`                 | Column constraints (length, nullable, …)           |

**Spring Data JPA** gives you CRUD for free: extend `JpaRepository<Entity, IdType>` and you get
`save`, `findById`, `findAll`, `deleteById`, `count`, `existsById`, … with **no implementation**.
You can also declare **derived queries** by method name, e.g.
`findByNameContainingIgnoreCase(String)` — Spring parses the name and writes the SQL.

See `model/Product.java` and `repository/ProductRepository.java`.

---

## 7. Web / REST annotations

| Annotation                       | Purpose                                                  |
| -------------------------------- | -------------------------------------------------------- |
| `@RequestMapping("/base")`       | Base path for the controller                             |
| `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping` | HTTP verb → method    |
| `@PathVariable`                  | Bind a `{id}` URL segment to a parameter                 |
| `@RequestParam`                  | Bind a `?query=` string parameter                        |
| `@RequestBody`                   | Deserialize the JSON request body into an object         |
| `@Valid`                         | Trigger Bean Validation on the bound object              |
| `ResponseEntity<T>`              | Full control over status code, headers, and body         |

See `controller/ProductController.java`.

---

## 8. Validation annotations (on the request DTO)

| Annotation          | Rule                                    |
| ------------------- | --------------------------------------- |
| `@NotBlank`         | Non-null and contains non-whitespace    |
| `@Size(max=…)`      | Length bounds                           |
| `@Positive`         | Number > 0                              |
| `@PositiveOrZero`   | Number ≥ 0                              |

A `@Valid` failure raises `MethodArgumentNotValidException`, which our
`@RestControllerAdvice` turns into a clean **400** response. See `dto/ProductRequest.java` and
`exception/GlobalExceptionHandler.java`.

---

## 9. Cross-cutting concerns

| Annotation                 | Purpose                                                          |
| -------------------------- | --------------------------------------------------------------- |
| `@Transactional`           | Run a method in a DB transaction (commit/rollback), via AOP proxy |
| `@RestControllerAdvice`    | Global exception handling across all controllers                 |
| `@ExceptionHandler(X)`     | Handle exception type `X` and build the HTTP response            |

---

## 10. Testing annotations

| Annotation                            | Scope loaded                              |
| ------------------------------------- | ----------------------------------------- |
| `@SpringBootTest`                     | The **whole** application context         |
| `@DataJpaTest`                        | JPA slice only (repos + embedded DB)      |
| `@WebMvcTest(X.class)`                | MVC slice for controller `X`              |
| `@MockBean`                           | Replace a bean with a Mockito mock        |
| `@ExtendWith(MockitoExtension.class)` | Enable `@Mock` / `@InjectMocks` (no Spring)|

Slice tests are fast because they load only what they need. Pure Mockito tests load **no** Spring
context at all — the reward for constructor injection + programming to interfaces.

See the `src/test` tree.

---

## Mental model summary

```
You write classes + annotations
        │
        ▼
Spring scans them at startup  ──►  builds beans (objects)  ──►  the IoC container
        │                                                            │
        │  injects each bean's dependencies (DI)                     │
        ▼                                                            ▼
Controller ──needs──► Service(interface) ──needs──► Repository ──► H2 database
```

You never call `new` for these — you declare *what* you need, and Spring provides it.
