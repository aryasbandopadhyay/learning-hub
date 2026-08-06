# 09. Count Ways to Group Overlapping Ranges

- **Difficulty:** Medium
- **Pattern:** interval merging & sweep line
- **Asked at:** Google, Amazon, Meta

## Problem
Given `ranges`, where each range is `[start, end]` and endpoints are inclusive, overlapping ranges must belong to the same group. Each connected group may be assigned to one of two buckets. Return the number of valid assignments modulo `1_000_000_007`.

Constraints: `1 <= len(ranges) <= 10^5`, `0 <= start <= end <= 10^9`.

## Examples
```text
Input: ranges = [[6,10],[5,15],[1,3],[10,20]]
Output: 4
Explanation: [1,3] is one group and the other three ranges form a connected group, so there are 2^2 assignments.
```

## Understanding & Intuition
Overlapping intervals are connected components on the number line. Once intervals are merged into connected groups, each group independently chooses one of two buckets. Therefore the answer is `2` raised to the number of merged groups.

## Approach 1 — Naive / Brute Force
**Idea:** Build an overlap graph and count connected components with DFS.
```python
class Solution:
    def countWays(self, ranges: list[list[int]]) -> int:
        MOD = 1_000_000_007
        n = len(ranges)
        graph = [[] for _ in range(n)]
        for i in range(n):
            for j in range(i + 1, n):
                a, b = ranges[i]
                c, d = ranges[j]
                if max(a, c) <= min(b, d):
                    graph[i].append(j)
                    graph[j].append(i)
        seen = [False] * n
        groups = 0
        for i in range(n):
            if not seen[i]:
                groups += 1
                stack = [i]
                seen[i] = True
                while stack:
                    u = stack.pop()
                    for v in graph[u]:
                        if not seen[v]:
                            seen[v] = True
                            stack.append(v)
        return pow(2, groups, MOD)
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Sort intervals and explicitly merge connected ranges, then count the merged ranges.
```python
class Solution:
    def countWays(self, ranges: list[list[int]]) -> int:
        MOD = 1_000_000_007
        ranges = sorted(ranges)
        merged = []
        for s, e in ranges:
            if not merged or s > merged[-1][1]:
                merged.append([s, e])
            else:
                merged[-1][1] = max(merged[-1][1], e)
        return pow(2, len(merged), MOD)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort and count groups on the fly without storing the merged intervals.
```python
class Solution:
    def countWays(self, ranges: list[list[int]]) -> int:
        MOD = 1_000_000_007
        ranges = sorted(ranges)
        groups = 0
        cur_end = -1
        for s, e in ranges:
            if s > cur_end:
                groups += 1
                cur_end = e
            else:
                cur_end = max(cur_end, e)
        return pow(2, groups, MOD)
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n^2) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Inclusive endpoints mean `[1,3]` and `[3,5]` overlap.
- Use modular exponentiation for large answers.
- Count connected merged groups, not pairwise overlap count.

## Related
- Accounts Merge
- Merge Intervals
