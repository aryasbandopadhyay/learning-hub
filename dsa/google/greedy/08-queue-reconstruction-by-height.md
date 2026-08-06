# 08. Queue Reconstruction by Height

- **Difficulty:** Medium
- **Pattern:** greedy
- **Asked at:** Google

## Problem
Each person is represented by `[height, k]`, where `k` is the number of people in front of them with height greater than or equal to `height`. Reconstruct and return a queue satisfying all pairs. For deterministic grading, return the standard queue obtained by processing taller people first and inserting each person at index `k`.

Constraints: `1 <= len(people) <= 2000`, `0 <= height <= 10^6`, `0 <= k < len(people)`, and a valid queue exists.

## Examples
```text
Input: people = [[7,0],[4,4],[7,1],[5,0],[6,1],[5,2]]
Output: [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]]
Explanation: Every person has exactly k taller-or-equal people before them.
```

## Understanding & Intuition
Shorter people do not affect the `k` values of taller people. Therefore, place taller people first, where each person's `k` is simply their final insertion index among already placed taller-or-equal people. Inserting shorter people later cannot invalidate taller constraints.

## Approach 1 — Naive / Brute Force
**Idea:** Generate the canonical sorted order, then build a new list by copying around the insertion point each time.
```python
class Solution:
    def reconstructQueue(self, people: list[list[int]]) -> list[list[int]]:
        order = sorted(people, key=lambda p: (-p[0], p[1]))
        queue = []
        for person in order:
            queue = queue[:person[1]] + [person[:]] + queue[person[1]:]
        return queue
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Use in-place list insertion after sorting by decreasing height and increasing `k`.
```python
class Solution:
    def reconstructQueue(self, people):
        queue = []
        for person in sorted(people, key=lambda p: (-p[0], p[1])):
            queue.insert(person[1], person[:])
        return queue
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Simulate the same insertions with a Fenwick tree over open slots, processing shorter people first.
```python
class Solution:
    def reconstructQueue(self, people):
        n = len(people)
        bit = [0] * (n + 1)
        def add(i, delta):
            i += 1
            while i <= n:
                bit[i] += delta
                i += i & -i
        def kth(k):
            idx = 0
            bitmask = 1 << (n.bit_length() - 1)
            while bitmask:
                nxt = idx + bitmask
                if nxt <= n and bit[nxt] < k:
                    idx = nxt
                    k -= bit[nxt]
                bitmask >>= 1
            return idx
        for i in range(n):
            add(i, 1)
        ans = [None] * n
        for h, k in sorted(people, key=lambda p: (p[0], -p[1])):
            pos = kth(k + 1)
            ans[pos] = [h, k]
            add(pos, -1)
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n^2) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Sort taller people before shorter people for insertion-based solutions.
- For equal heights, smaller `k` must be inserted first.
- Return copied inner lists to avoid accidental aliasing surprises.

## Related
- Insert Interval
- Order Statistic Tree
