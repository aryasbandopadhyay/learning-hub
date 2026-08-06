# 05. Describe the Painting

- **Difficulty:** Medium
- **Pattern:** interval merging & sweep line
- **Asked at:** Google, Amazon, Meta

## Problem
You are given painting `segments`, where each segment is `[start, end, color]` and paints every point in the half-open interval `[start, end)`. Return a list of non-overlapping segments `[start, end, sumColor]` describing all painted portions after mixing overlapping colors.

Constraints: `1 <= len(segments) <= 2 * 10^4`, `1 <= start < end <= 10^5`, and `1 <= color <= 10^9`.

## Examples
```text
Input: segments = [[1,4,5],[4,7,7],[1,7,9]]
Output: [[1,4,14],[4,7,16]]
Explanation: Color 9 overlaps with color 5 on [1,4) and with color 7 on [4,7).
```

## Understanding & Intuition
Colors add over continuous ranges, and the sum can change only where some segment starts or ends. Sweeping endpoints while tracking the current color sum naturally emits maximal painted pieces.

## Approach 1 — Naive / Brute Force
**Idea:** For each elementary interval between distinct endpoints, scan all segments and compute the mixed color.
```python
class Solution:
    def splitPainting(self, segments: list[list[int]]) -> list[list[int]]:
        points = sorted(set([x for s, e, c in segments for x in (s, e)]))
        ans = []
        for i in range(len(points) - 1):
            a, b = points[i], points[i + 1]
            total = 0
            for s, e, c in segments:
                if s <= a and b <= e:
                    total += c
            if total:
                if ans and ans[-1][1] == a and ans[-1][2] == total:
                    ans[-1][1] = b
                else:
                    ans.append([a, b, total])
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Store color deltas in a sorted map represented by a dictionary plus sorted keys.
```python
class Solution:
    def splitPainting(self, segments: list[list[int]]) -> list[list[int]]:
        delta = {}
        for s, e, c in segments:
            delta[s] = delta.get(s, 0) + c
            delta[e] = delta.get(e, 0) - c
        keys = sorted(delta)
        ans = []
        cur = 0
        for i, x in enumerate(keys[:-1]):
            cur += delta[x]
            y = keys[i + 1]
            if cur:
                if ans and ans[-1][1] == x and ans[-1][2] == cur:
                    ans[-1][1] = y
                else:
                    ans.append([x, y, cur])
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Since coordinates are bounded, use a difference array and scan only the touched coordinate span.
```python
class Solution:
    def splitPainting(self, segments: list[list[int]]) -> list[list[int]]:
        if not segments:
            return []
        max_x = max(e for _, e, _ in segments)
        diff = [0] * (max_x + 1)
        touched = [False] * (max_x + 1)
        for s, e, c in segments:
            diff[s] += c
            diff[e] -= c
            touched[s] = True
            touched[e] = True
        ans = []
        cur = 0
        last = None
        for x in range(max_x + 1):
            if touched[x]:
                if last is not None and last < x and cur:
                    if ans and ans[-1][1] == last and ans[-1][2] == cur:
                        ans[-1][1] = x
                    else:
                        ans.append([last, x, cur])
                cur += diff[x]
                last = x
        return ans
```
- **Time:** O(n + U) — **Space:** O(U)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n + U) | O(U) |

## Edge Cases & Pitfalls
- Intervals are half-open, so adjacent segments do not overlap at an endpoint.
- Do not output unpainted zero-color ranges.
- Merge adjacent output ranges with the same mixed color.

## Related
- Range Addition
- The Skyline Problem
