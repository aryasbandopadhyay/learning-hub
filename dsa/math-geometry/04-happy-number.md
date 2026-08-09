# 04. Happy Number

- **Difficulty:** Easy
- **Pattern:** Math & Geometry
- **Asked at:** Amazon, Google, Microsoft

## Problem
A positive integer is called **happy** if repeatedly replacing it by the sum of the squares of its decimal digits eventually reaches `1`.

Given an integer `n`, determine whether it is happy. If the process enters a cycle that does not contain `1`, then `n` is not happy.

**Input**
- `n`: a positive integer.

**Output**
- `True` if the digit-square process reaches `1`; otherwise `False`.

## Constraints
- `1 <= n <= 2^31 - 1`

## Examples
```text
Input: n = 19
Output: True
Explanation: Starting from `19`: `1^2 + 9^2 = 82`, then `8^2 + 2^2 = 68`, then `6^2 + 8^2 = 100`, then `1^2 + 0^2 + 0^2 = 1`, so the process reaches `1`.
```

```text
Input: n = 2
Output: False
Explanation: The sequence eventually repeats without reaching `1`, so `2` is not happy.
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
