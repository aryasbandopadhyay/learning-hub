# 04. Happy Number

- **Difficulty:** Easy
- **Pattern:** Math & Geometry
- **Asked at:** Amazon, Google, Microsoft

## Problem
Given a positive integer `n`, repeatedly replace it by the sum of squares of its digits. Return `True` if the process reaches `1`; otherwise it cycles and return `False`. Constraints: `1 <= n <= 2^31 - 1`.

## Examples
```text
Input: n = 19
Output: True
Explanation: 1²+9²=82, 8²+2²=68, 6²+8²=100, 1²=1.
```

## Understanding & Intuition
The sequence either reaches `1` or repeats a previous value. Repetition means an infinite cycle. Cycle detection is therefore enough.

## Approach 1 — Naive / Brute Force
**Idea:** Use a fixed iteration cap; values quickly shrink for 32-bit inputs.
```python
class Solution:
    def isHappy(self, n: int) -> bool:
        def nxt(x: int) -> int:
            total = 0
            while x:
                digit = x % 10
                total += digit * digit
                x //= 10
            return total

        for _ in range(1000):
            if n == 1:
                return True
            n = nxt(n)
        return False
```
- **Time:** O(k log n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Store seen numbers; a repeated value proves a cycle.
```python
class Solution:
    def isHappy(self, n: int) -> bool:
        def nxt(x: int) -> int:
            return sum(int(ch) ** 2 for ch in str(x))

        seen = set()
        while n != 1 and n not in seen:
            seen.add(n)
            n = nxt(n)
        return n == 1
```
- **Time:** O(k log n) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Use Floyd's slow and fast pointers to detect the cycle without a set.
```python
class Solution:
    def isHappy(self, n: int) -> bool:
        def nxt(x: int) -> int:
            total = 0
            while x:
                digit = x % 10
                total += digit * digit
                x //= 10
            return total

        slow, fast = n, nxt(n)
        while fast != 1 and slow != fast:
            slow = nxt(slow)
            fast = nxt(nxt(fast))
        return fast == 1
```
- **Time:** O(k log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(k log n) | O(1) |
| Better | O(k log n) | O(k) |
| Optimal | O(k log n) | O(1) |

## Edge Cases & Pitfalls
- `1` is already happy.
- Do not assume every sequence decreases monotonically.

## Related
- Linked List Cycle
- Add Digits
