# 15. Decorators, Closures & Scope

> Decorators, closures, and scope explain how Python binds names and transforms functions, making them frequent interview topics for practical language mastery.

## Core Concepts
### LEGB
Name lookup checks Local, Enclosing, Global, then Builtins scopes. Assignment creates a local unless declared `global` or `nonlocal`.

### Closures
A closure is a function plus remembered bindings from enclosing scopes, stored in cell objects.

### Decorators
A decorator is called at definition time with the decorated object and its return value is rebound to the original name.

## How It Works Internally
The compiler classifies locals, free variables, and cell variables. Bytecode uses closure cells for variables captured by nested functions. Decorator syntax is equivalent to defining the function, then assigning `name = decorator(name)`. Wrappers should use `functools.wraps` to preserve metadata useful for debugging and introspection.

## Code Examples

```python
import functools

def call_counter(func):
    count = 0
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        nonlocal count
        count += 1
        print(f'{func.__name__} call #{count}')
        return func(*args, **kwargs)
    return wrapper

@call_counter
def greet(name):
    return f'hello {name}'

print(greet('Ada'))
print(greet('Grace'))
print(greet.__name__)
```

## Common Interview Questions
- **Q:** What is LEGB? **A:** Local, Enclosing, Global, Builtins.
- **Q:** What is a closure? **A:** Function retaining enclosing-scope bindings.
- **Q:** Why `nonlocal`? **A:** To rebind an enclosing non-global variable.
- **Q:** What is a decorator? **A:** Callable that receives and replaces a function/class.
- **Q:** Why `functools.wraps`? **A:** Preserves metadata like name and docstring.
- **Q:** When decorators run? **A:** At definition execution time, often import time.
- **Q:** Late binding pitfall? **A:** Loop variables are looked up when inner functions run.

## Pitfalls & Best Practices
- Use `functools.wraps` in wrappers.
- Capture loop variables deliberately.
- Keep decorators transparent and small.
- Avoid hidden unsynchronized global state.

## Related Topics
- Python Data Model & Objects
- Iterators, Generators & Coroutines
- concurrent.futures
