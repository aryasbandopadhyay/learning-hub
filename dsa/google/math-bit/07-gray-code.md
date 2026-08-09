# 07. Gray Code

- **Difficulty:** Medium
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
Given an integer `n`, return the canonical n-bit Gray code sequence of length `2^n`. The canonical sequence starts at `0`, and entry `i` is `i XOR (i >> 1)`.

Implement `Solution.grayCode` with the parameters below and return the requested value.

**Input**
- `n`: a `int`; the size/count parameter described above.

**Output**
- The canonical Gray-code sequence where element `i` equals `i XOR (i >> 1)`, in increasing `i` order.

## Constraints
- `0 <= n <= 16`

## Examples
```text
Input: n = 3
Output: [0, 1, 3, 2, 6, 7, 5, 4]
Explanation: Adjacent values differ by exactly one bit, including the wraparound pair. The result is shown in the required order.
```

## Understanding & Intuition
A Gray code orders bit patterns so consecutive entries differ by one bit. Reflecting the previous sequence and prefixing the reflected half with a new high bit preserves the one-bit difference. The closed form `i ^ (i >> 1)` gives the canonical reflected sequence directly.

## Approach 1 — Naive / Brute Force
**Idea:** Backtrack through all bitmasks, always choosing the smallest unused one-bit neighbor to obtain the canonical order.
```python
class Solution:
    def grayCode(self, n: int) -> list[int]:
        total = 1 << n
        path = [0]
        used = {0}
        def dfs(x):
            if len(path) == total:
                return (path[-1] ^ path[0]).bit_count() == 1 or total == 1
            for bit in range(n):
                y = x ^ (1 << bit)
                if y not in used:
                    used.add(y)
                    path.append(y)
                    if dfs(y):
                        return True
                    path.pop()
                    used.remove(y)
            return False
        dfs(0)
        return path
```
- **Time:** O(2^n * n) — **Space:** O(2^n)

## Approach 2 — Better
**Idea:** Iteratively reflect the current sequence and add the next high bit to the reflected half.
```python
class Solution:
    def grayCode(self, n: int) -> list[int]:
        ans = [0]
        for bit in range(n):
            add = 1 << bit
            for x in reversed(ans):
                ans.append(add | x)
        return ans
```
- **Time:** O(2^n) — **Space:** O(2^n)

## Approach 3 — Optimal
**Idea:** Use the canonical formula for each index independently.
```python
class Solution:
    def grayCode(self, n: int) -> list[int]:
        return [i ^ (i >> 1) for i in range(1 << n)]
```
- **Time:** O(2^n) — **Space:** O(2^n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n n) | O(2^n) |
| Better | O(2^n) | O(2^n) |
| Optimal | O(2^n) | O(2^n) |

## Edge Cases & Pitfalls
- `n = 0` should return `[0]`.
- Use a canonical order because many Gray code sequences are valid.
- The reflected construction appends values in reverse order of the previous list.

## Related
- Counting Bits
- Reverse Bits
