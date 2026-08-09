# 12. Smallest Good Base

- **Difficulty:** Hard
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
For an integer `n` represented as a decimal string, return the smallest good base as a string. A base `k >= 2` is good if `n` can be written as `111...111` in base `k` with at least two ones.

Implement `Solution.smallestGoodBase` with the parameters below and return the requested value.

**Input**
- `n`: a `str`; the size/count parameter described above.

**Output**
- A `str` value representing the result described above.

## Constraints
- `3 <= int(n) <= 10^18`

## Examples
```text
Input: n = "4681"
Output: "8"
Explanation: 4681 = 1 + 8 + 8^2 + 8^3 + 8^4, so its base-8 representation is 11111.
```

## Understanding & Intuition
A representation of `m + 1` ones has value `1 + k + ... + k^m`. For a fixed length, this sum increases with `k`, so binary search can test whether a base exists. Trying longer representations first guarantees the smallest base.

## Approach 1 — Naive / Brute Force
**Idea:** Try each possible representation length and binary-search a base whose repeated-one sum matches `n`.
```python
class Solution:
    def smallestGoodBase(self, n: str) -> str:
        N = int(n)
        for length in range(N.bit_length(), 2, -1):
            lo, hi = 2, N - 1
            while lo <= hi:
                base = (lo + hi) // 2
                total = 0
                for _ in range(length):
                    total = total * base + 1
                    if total > N:
                        break
                if total == N:
                    return str(base)
                if total < N:
                    lo = base + 1
                else:
                    hi = base - 1
        return str(N - 1)
```
- **Time:** O(log^3 n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** For each possible number of ones, estimate the base near the integer root and verify the geometric sum.
```python
class Solution:
    def smallestGoodBase(self, n: str) -> str:
        N = int(n)
        max_m = N.bit_length() - 1
        for m in range(max_m, 1, -1):
            k = int(N ** (1.0 / m))
            for base in range(max(2, k - 2), k + 3):
                total, cur = 1, 1
                for _ in range(m):
                    cur *= base
                    total += cur
                    if total > N:
                        break
                if total == N:
                    return str(base)
        return str(N - 1)
```
- **Time:** O(log^2 n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** For each length from largest to smallest, binary search the base and compare the geometric sum with overflow-aware early stopping.
```python
class Solution:
    def smallestGoodBase(self, n: str) -> str:
        N = int(n)
        max_m = N.bit_length() - 1
        for m in range(max_m, 1, -1):
            lo, hi = 2, int(N ** (1.0 / m)) + 2
            while lo <= hi:
                base = (lo + hi) // 2
                total, cur = 1, 1
                for _ in range(m):
                    cur *= base
                    total += cur
                    if total > N:
                        break
                if total == N:
                    return str(base)
                if total < N:
                    lo = base + 1
                else:
                    hi = base - 1
        return str(N - 1)
```
- **Time:** O(log^3 n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(log^3 n) | O(1) |
| Better | O(log^2 n) | O(1) |
| Optimal | O(log^3 n) | O(1) |

## Edge Cases & Pitfalls
- The fallback base is always `n - 1`, representing `11`.
- Try longer lengths first to find the smallest base.
- Floating roots are only estimates; always verify by exact integer summation.

## Related
- Super Pow
- Nth Digit
