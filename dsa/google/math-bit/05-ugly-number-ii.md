# 05. Ugly Number II

- **Difficulty:** Medium
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
An ugly number is a positive integer whose prime factors are limited to `2`, `3`, and `5`. Given `n`, return the `n`th ugly number.

Implement `Solution.nthUglyNumber` with the parameters below and return the requested value.

**Input**
- `n`: a `int`; the size/count parameter described above.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= n <= 1690`

## Examples
```text
Input: n = 15
Output: 24
Explanation: The first 15 ugly numbers end with 16, 18, 20, and 24.
```

## Understanding & Intuition
Every ugly number after 1 is formed by multiplying an earlier ugly number by 2, 3, or 5. The challenge is to generate them in sorted order without duplicates. Three pointers track the next candidate multiple for each factor.

## Approach 1 — Naive / Brute Force
**Idea:** Generate every ugly number within the 32-bit answer range from powers of 2, 3, and 5, then sort them.
```python
class Solution:
    def nthUglyNumber(self, n: int) -> int:
        vals = set()
        a = 1
        while a <= 2**31:
            b = a
            while b <= 2**31:
                c = b
                while c <= 2**31:
                    vals.add(c)
                    c *= 5
                b *= 3
            a *= 2
        return sorted(vals)[n - 1]
```
- **Time:** O(U log U) — **Space:** O(U)

## Approach 2 — Better
**Idea:** Use a min-heap and a set to repeatedly pop the smallest unseen ugly number and push its next multiples.
```python
class Solution:
    def nthUglyNumber(self, n: int) -> int:
        import heapq
        heap = [1]
        seen = {1}
        cur = 1
        for _ in range(n):
            cur = heapq.heappop(heap)
            for p in (2, 3, 5):
                nxt = cur * p
                if nxt not in seen:
                    seen.add(nxt)
                    heapq.heappush(heap, nxt)
        return cur
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain indices for the next multiple by 2, 3, and 5, appending the smallest candidate and advancing every pointer that produced it.
```python
class Solution:
    def nthUglyNumber(self, n: int) -> int:
        ugly = [1]
        i2 = i3 = i5 = 0
        while len(ugly) < n:
            nxt = min(ugly[i2] * 2, ugly[i3] * 3, ugly[i5] * 5)
            ugly.append(nxt)
            if nxt == ugly[i2] * 2:
                i2 += 1
            if nxt == ugly[i3] * 3:
                i3 += 1
            if nxt == ugly[i5] * 5:
                i5 += 1
        return ugly[-1]
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(U log U) | O(U) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- `1` is considered the first ugly number.
- Advance all matching pointers to avoid duplicates like `6`.
- Heap generation needs a set because the same value may arise from different factors.

## Related
- Super Pow
- Perfect Squares
