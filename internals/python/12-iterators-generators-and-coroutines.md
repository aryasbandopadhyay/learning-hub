# 12. Iterators, Generators & Coroutines

> Iterators and generators enable lazy data processing; their suspension model also prepares candidates to reason about coroutines and async execution.

## Core Concepts
### Iterator protocol
An iterable returns an iterator from `__iter__`; an iterator returns itself and produces values from `__next__` until `StopIteration`.

### Generators
A generator function contains `yield`. Calling it creates a generator object, and each `next()` resumes its suspended frame.

### Lazy pipelines
Generator expressions and functions compose into memory-efficient pipelines that process one item at a time.

## How It Works Internally
Generator objects store a frame, instruction pointer, locals, and evaluation stack. `yield` returns a value and suspends execution. `send`, `throw`, and `close` add coroutine-like control. Native `async def` coroutines use a related suspension model with `await` instead of plain `yield`.

## Code Examples

```python
def numbers(limit):
    current = 0
    while current < limit:
        yield current
        current += 1

def squares(values):
    for value in values:
        yield value * value

print(list(squares(n for n in numbers(5) if n % 2 == 0)))
it = iter(['a', 'b'])
print(next(it))
print(next(it))
```

## Common Interview Questions
- **Q:** Iterable vs iterator? **A:** Iterable produces an iterator; iterator tracks state and returns values.
- **Q:** What ends iteration? **A:** `StopIteration`.
- **Q:** What does yield do? **A:** Emits a value and suspends the frame.
- **Q:** Why generators? **A:** Lazy evaluation and low memory use.
- **Q:** Can generators restart? **A:** No, create a new generator.
- **Q:** Generator expression vs list comprehension? **A:** Lazy vs eager list materialization.
- **Q:** What is `yield from`? **A:** Delegation to a sub-iterator.

## Pitfalls & Best Practices
- Do not expect exhausted iterators to reset.
- Use `finally` for generator resource cleanup.
- Prefer generator pipelines for streaming.
- Avoid accidental `StopIteration` leaks inside generators.

## Related Topics
- asyncio Fundamentals
- Python Data Model & Objects
- Memory Management & Garbage Collection
