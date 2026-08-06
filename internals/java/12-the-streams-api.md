# 12. The Streams API

> Streams express bulk data processing as lazy pipelines; interviews test laziness, collectors, side effects, and parallel-stream judgement.

## Core Concepts
### Pipeline
A stream has a source, intermediate operations, and a terminal operation. Intermediate operations are lazy.

### Operation Types
Stateless intermediate operations include `map` and `filter`; stateful ones include `sorted`, `distinct`, `limit`, and `skip`. Terminal operations include `collect`, `reduce`, `count`, `forEach`, and matching operations.

### Collectors
Collectors define supplier, accumulator, combiner, finisher, and characteristics for mutable reduction.

### Parallel Streams
Use for CPU-bound, independent, associative work over large efficiently splittable sources.

## How It Works Internally
The implementation fuses stages so an element can flow through several operations in one traversal. Short-circuiting terminal operations may stop early. `Spliterator` drives traversal and splitting, advertising characteristics like `SIZED`, `ORDERED`, and `CONCURRENT`. Parallel streams generally use the common ForkJoinPool and require stateless, non-interfering functions for correctness.

## Code Examples
```java
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamsDemo {
    record Employee(String department, String name, int salary) {}
    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("eng", "Ada", 120),
                new Employee("eng", "Grace", 130),
                new Employee("sales", "Linus", 90));
        Map<String, Double> avg = employees.stream()
                .filter(e -> e.salary() >= 100)
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.averagingInt(Employee::salary)));
        System.out.println(avg);
    }
}
```

## Common Interview Questions
- **Q:** Intermediate vs terminal? **A:** Intermediate builds a lazy pipeline; terminal executes it.
- **Q:** What is laziness? **A:** Work happens only when demanded by a terminal operation.
- **Q:** map vs flatMap? **A:** Transform one-to-one versus transform to streams and flatten.
- **Q:** reduce vs collect? **A:** Immutable combining versus mutable reduction.
- **Q:** When parallel streams? **A:** Large CPU-bound independent associative tasks.
- **Q:** Why avoid side effects? **A:** They violate non-interference and can race in parallel.

## Pitfalls & Best Practices
- Keep lambdas stateless.
- Avoid parallel streams for blocking I/O.
- Use primitive streams for numeric hot paths.
- Measure before optimizing loops into streams.
- Use collectors instead of external mutation.

## Related Topics
- Futures & Async
- The Java Collections Framework
- Concurrent Collections
