# 16. Class Loading & JVM Execution

> Class loading and JVM execution explain how bytecode becomes optimized machine code, including delegation, initialization, verification, JIT, and deoptimization.

## Core Concepts
### Phases
Classes are loaded, linked, and initialized. Linking includes verification, preparation, and resolution.

### Class Loaders
Bootstrap, platform, application, and custom loaders define namespaces. Parent delegation usually asks the parent first.

### Initialization
Static initialization runs on first active use: instance creation, static method invocation, non-constant static field access, and some reflection.

### Execution
The JVM verifies bytecode, interprets initially, profiles hot code, and JIT-compiles frequently executed methods.

## How It Works Internally
Verification checks stack types, control flow, access rules, and bytecode structure. Preparation allocates static fields with default values; resolution turns symbolic constant-pool references into direct references. HotSpot tiered compilation uses interpreter profiling, C1, and C2. Optimizations include inlining, escape analysis, scalar replacement, lock elimination, and dead-code elimination. Deoptimization returns to interpreted execution when speculation fails. Class identity is binary name plus defining loader.

## Code Examples
```java
public class ClassLoadingDemo {
    static { System.out.println("initialized"); }
    private static final int CONSTANT = 42;
    private static final Integer RUNTIME_VALUE = Integer.valueOf(42);

    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> type = Class.forName("ClassLoadingDemo", false,
                ClassLoadingDemo.class.getClassLoader());
        System.out.println(type.getName());

        System.out.println(RUNTIME_VALUE); // active use can trigger initialization
        System.out.println(CONSTANT);      // compile-time constants may be inlined
    }
}
```

## Common Interview Questions
- **Q:** Load/link/init? **A:** Load bytes; link verify/prepare/resolve; initialize static code.
- **Q:** Parent delegation? **A:** Ask parent loader before trying child loader.
- **Q:** Why loader identity matters? **A:** Same binary name from different defining loaders is different type.
- **Q:** When initialize? **A:** First active use.
- **Q:** What is verification? **A:** Safety checks for bytecode type/control/access correctness.
- **Q:** Interpreter vs JIT? **A:** Interpreter starts/profiles; JIT compiles hot code.
- **Q:** What is deoptimization? **A:** Fallback from speculative compiled code to interpreter.

## Pitfalls & Best Practices
- Do not assume initialization order across unrelated classes.
- Be careful with custom-loader leaks.
- Remember constants can be inlined.
- Warm up before benchmarking.
- Avoid relying on class unloading timing.

## Related Topics
- JVM Memory & Garbage Collection
- The Java Memory Model
- Thread Pools & the Executor Framework
