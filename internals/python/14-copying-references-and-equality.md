# 14. Copying, References & Equality

> Assignment, shallow copy, deep copy, identity, equality, and interning determine whether Python code shares objects or duplicates data.

## Core Concepts
### Assignment
`a = b` binds another name to the same object. It does not copy.

### Shallow and deep copy
A shallow copy duplicates only the outer container. A deep copy recursively copies reachable objects while preserving cycles/shared references through a memo table.

### Equality and identity
`==` calls equality logic. `is` compares object identity. Interning may reuse immutable objects but should not be relied on for equality.

## How It Works Internally
Names live in namespaces or optimized local slots and hold object references. `copy.copy` and `copy.deepcopy` consult type-specific protocols such as `__copy__` and `__deepcopy__`. Rich comparison methods implement equality, while identity is essentially pointer comparison in CPython.

## Code Examples

```python
import copy

original = [[1, 2], [3, 4]]
alias = original
shallow = copy.copy(original)
deep = copy.deepcopy(original)
original[0].append(99)
print(alias is original)
print(shallow is original, shallow[0] is original[0])
print(deep[0] == original[0], deep[0] is original[0])
print(1000 == int('1000'))
```

## Common Interview Questions
- **Q:** Does assignment copy? **A:** No, it binds a name to an object.
- **Q:** Shallow copy effect? **A:** New outer object, shared nested references.
- **Q:** Deep copy effect? **A:** Recursive copy with memoization.
- **Q:** `is` vs `==`? **A:** Identity vs equality.
- **Q:** When use `is`? **A:** For `None`, booleans, and sentinels.
- **Q:** What is interning? **A:** Implementation reuse of immutable objects.
- **Q:** Mutable default pitfall? **A:** Default object is created once and reused.

## Pitfalls & Best Practices
- Use `is None`, not `== None`.
- Do not depend on small-int/string interning.
- Be explicit about nested mutable copies.
- Prefer immutability for shared data.

## Related Topics
- Python Data Model & Objects
- Memory Management & Garbage Collection
- The Collections Framework
