# 01. The CPython Execution Model

> CPython turns source into bytecode and executes it in a stack-based virtual machine; knowing this helps explain imports, frames, performance, and memory behavior in interviews.

## Core Concepts
### Compilation pipeline
Source is tokenized, parsed to an AST, compiled to a code object, and interpreted as bytecode. Bytecode is not a language guarantee; it is CPython implementation detail.

### Frames and namespaces
Each function call creates a frame containing locals, globals, builtins, an operand stack, and an instruction pointer. Names are references to objects, not storage boxes.

### Import caching
Imported modules may be cached as `.pyc` files in `__pycache__`; this skips parsing/compilation for unchanged modules but does not create native code.

## How It Works Internally
The CPython evaluation loop dispatches bytecode instructions. Code objects hold constants, names, local variable metadata, exception tables, and bytecode. Frames execute those code objects. CPython objects are heap values with a reference count and type pointer; executing bytecode creates, passes, stores, and releases object references. When a reference count reaches zero the object is normally deallocated immediately, while cycles are handled by cyclic GC.

## Code Examples

```python
import dis
import sys

def area(width, height=1):
    total = width * height
    return total

print(area(3, 4))
print(area.__code__.co_varnames)
print(area.__code__.co_consts)
print(sys.getrefcount(area) > 0)
dis.dis(area)
```

## Common Interview Questions
- **Q:** Does Python compile code? **A:** Yes. CPython compiles source to bytecode before interpretation.
- **Q:** Are `.pyc` files machine code? **A:** No. They are serialized bytecode caches.
- **Q:** What is a frame? **A:** Runtime state for one executing code object.
- **Q:** Why can bytecode change? **A:** It is an interpreter implementation detail.
- **Q:** What is reference counting? **A:** A per-object count of strong references.
- **Q:** Why can `getrefcount` look high? **A:** The function call itself adds a temporary reference.
- **Q:** Is CPython the only Python? **A:** No; PyPy, Jython, and IronPython differ internally.

## Pitfalls & Best Practices
- Do not rely on bytecode stability across versions.
- Use `.pyc` knowledge to reason about imports, not hot-loop speed.
- Remember CPython details may not apply to all implementations.
- Use `dis` for inspection and learning.

## Related Topics
- The GIL — Global Interpreter Lock
- Memory Management & Garbage Collection
- Python Data Model & Objects
