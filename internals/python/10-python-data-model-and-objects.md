# 10. Python Data Model & Objects

> Python’s data model connects syntax to objects, identity, mutability, and special methods; it is the foundation for explaining “Pythonic” behavior precisely.

## Core Concepts
### Object fundamentals
Everything important is an object: integers, functions, classes, modules, exceptions. Names bind references to objects.

### Identity, type, value
`id()` identifies an object during its lifetime, `type()` returns its class, and value is logical state. Mutability means value can change without identity changing.

### Dunder protocols
Special methods such as `__len__`, `__iter__`, and `__contains__` let objects participate in built-ins, operators, and syntax.

## How It Works Internally
In CPython, each object begins with a reference count and a pointer to its type object. Type objects contain slots for numeric, sequence, mapping, call, attribute, and iterator behavior. Built-ins usually dispatch through these protocol slots, not by ordinary instance attribute lookup alone.

## Code Examples

```python
class Bag:
    def __init__(self, items):
        self._items = list(items)
    def __len__(self):
        return len(self._items)
    def __iter__(self):
        return iter(self._items)
    def __contains__(self, item):
        return item in self._items

bag = Bag(['pen', 'book'])
print(len(bag))
print('pen' in bag)
print([x.upper() for x in bag])
```

## Common Interview Questions
- **Q:** Are variables objects? **A:** Names are references; objects hold data and behavior.
- **Q:** What is mutability? **A:** Value can change in place without identity changing.
- **Q:** `is` vs `==`? **A:** Identity comparison vs equality protocol.
- **Q:** What are dunders? **A:** Special methods implementing language protocols.
- **Q:** What does `len(x)` use? **A:** The length protocol, typically `__len__`.
- **Q:** Are classes objects? **A:** Yes, usually instances of `type`.
- **Q:** Why use built-ins? **A:** They invoke protocols clearly and correctly.

## Pitfalls & Best Practices
- Use `is` for singletons, not value equality.
- Implement related methods consistently, especially equality and hashing.
- Keep protocol behavior unsurprising.
- Remember assignment rebinds names.

## Related Topics
- Copying, References & Equality
- Iterators, Generators & Coroutines
- Decorators, Closures & Scope
