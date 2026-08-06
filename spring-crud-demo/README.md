# Spring Boot CRUD Demo — Learn Annotations, IoC & DI

A deliberately small, **heavily-commented** Spring Boot application that manages a catalog of
`Product` records. Its purpose is educational: every Java file explains the annotations, syntax,
and APIs it uses so you can learn **Spring Boot annotations, Inversion of Control (IoC), and
Dependency Injection (DI)** by reading real, working code.

> This app was produced via the **coding-orchestrator** pipeline (design → SOLID/design-pattern
> scrutiny → implementation → scrutiny-to-harmony → tests). See `docs/DESIGN.md`.

---

## Tech stack

| Concern            | Choice                                   |
| ------------------ | ---------------------------------------- |
| Framework          | Spring Boot 3.5.16                        |
| Language / JDK     | Java 17+ (verified on JDK 25)            |
| Persistence        | Spring Data JPA (Hibernate)              |
| Database           | **H2 in-memory** (no install needed)     |
| Validation         | Jakarta Bean Validation                  |
| Build              | Maven                                     |
| Tests              | JUnit 5, Mockito, MockMvc, AssertJ       |

---

## How to run

```powershell
# from the spring-crud-demo directory
mvn spring-boot:run
```

The app starts on **http://localhost:8080**. Sample products are seeded at startup
(see `config/DataInitializer.java`).

### H2 database console
Open **http://localhost:8080/h2-console** and connect with:
- **JDBC URL:** `jdbc:h2:mem:crud_demo`
- **User:** `sa`  •  **Password:** *(blank)*

---

## REST API

Base path: `/api/products`

| Method | Path                       | Description                | Success |
| ------ | -------------------------- | -------------------------- | ------- |
| GET    | `/api/products`            | List all products          | 200     |
| GET    | `/api/products?name=key`   | Search by name fragment    | 200     |
| GET    | `/api/products/{id}`       | Get one product            | 200/404 |
| POST   | `/api/products`            | Create a product           | 201     |
| PUT    | `/api/products/{id}`       | Update a product           | 200/404 |
| DELETE | `/api/products/{id}`       | Delete a product           | 204/404 |

### curl examples

```bash
# Create
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Webcam","description":"1080p","price":59.99,"quantity":10}'

# List
curl http://localhost:8080/api/products

# Get one
curl http://localhost:8080/api/products/1

# Search
curl "http://localhost:8080/api/products?name=web"

# Update
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Webcam Pro","description":"4K","price":99.99,"quantity":8}'

# Delete
curl -X DELETE http://localhost:8080/api/products/1
```

### Validation errors
Sending an invalid body (e.g. blank `name`, negative `price`) returns **400** with a body like:

```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": { "name": "name is required" }
}
```

---

## Project layout

```
src/main/java/com/example/crud
├── CrudDemoApplication.java        # @SpringBootApplication entry point
├── model/Product.java              # @Entity — the JPA domain model
├── repository/ProductRepository.java   # @Repository — Spring Data JPA
├── dto/
│   ├── ProductRequest.java         # input DTO + Bean Validation
│   └── ProductResponse.java        # output DTO
├── mapper/ProductMapper.java       # @Component — entity <-> DTO
├── service/
│   ├── ProductService.java         # interface (DIP abstraction)
│   └── impl/ProductServiceImpl.java# @Service — business logic + DI
├── controller/ProductController.java   # @RestController — HTTP layer
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── ApiError.java
│   └── GlobalExceptionHandler.java # @RestControllerAdvice
└── config/DataInitializer.java     # @Configuration + @Bean seeding
```

The request flow: **Controller → Service (interface) → Repository → H2**, with DTOs at the edges.

---

## Run the tests

```powershell
mvn test
```

Covers four test styles:
- `@SpringBootTest` — full context smoke test
- `@DataJpaTest` — repository slice against embedded H2
- Mockito unit test — pure service logic, no Spring
- `@WebMvcTest` + MockMvc — controller/web slice

---

## Where to learn

Read the files in this order, they build on each other:
1. `CrudDemoApplication.java` — what `@SpringBootApplication` bootstraps
2. `model/Product.java` — JPA entity mapping
3. `repository/ProductRepository.java` — free CRUD via Spring Data
4. `service/ProductService.java` + `impl/ProductServiceImpl.java` — **DI & IoC core**
5. `controller/ProductController.java` — request mapping annotations
6. `config/DataInitializer.java` — `@Bean` / IoC container

For a concise cheat-sheet of every annotation used, see **`docs/LEARNING.md`**.
