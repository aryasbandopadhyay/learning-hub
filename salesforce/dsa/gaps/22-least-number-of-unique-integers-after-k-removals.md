# 22. Least Number of Unique Integers after K Removals

- **Difficulty:** Medium
- **Pattern:** Greedy / Hash Map
- **Asked at:** Salesforce, Amazon, Microsoft

## Problem
Remove exactly `k` elements from an array so the number of unique integers remaining is minimized.

## Examples
```text
Input: arr = [5,5,4], k = 1
Output: 1
Explanation: Remove 4, leaving only unique value 5.
```

## Understanding & Intuition
Removing all copies of a low-frequency value eliminates one unique integer at the lowest cost, so greedily remove frequencies from smallest to largest.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly find the current least frequent remaining value.
```python
class Solution:
    def findLeastNumOfUniqueInts(self, arr: list[int], k: int) -> int:
        counts = {}
        for x in arr: counts[x] = counts.get(x, 0) + 1
        while k and counts:
            x = min(counts, key=lambda v: counts[v])
            if counts[x] > k: break
            k -= counts[x]; del counts[x]
        return len(counts)
```
- **Time:** O(u^2) — **Space:** O(u)

## Approach 2 — Better
**Idea:** Sort frequencies and remove complete values greedily.
```python
class Solution:
    def findLeastNumOfUniqueInts(self, arr: list[int], k: int) -> int:
        counts = {}
        for x in arr: counts[x] = counts.get(x, 0) + 1
        remaining = len(counts)
        for freq in sorted(counts.values()):
            if k < freq: break
            k -= freq; remaining -= 1
        return remaining
```
- **Time:** O(n + u log u) — **Space:** O(u)

## Approach 3 — Optimal
**Idea:** Bucket frequencies and process buckets from low to high.
```python
class Solution:
    def findLeastNumOfUniqueInts(self, arr: list[int], k: int) -> int:
        counts = {}
        for x in arr: counts[x] = counts.get(x, 0) + 1
        buckets = [0] * (len(arr) + 1)
        for f in counts.values(): buckets[f] += 1
        remaining = len(counts)
        for f in range(1, len(buckets)):
            take = min(buckets[f], k // f)
            remaining -= take; k -= take * f
            if k < f: break
        return remaining
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(u^2) | O(u) |
| Better | O(n + u log u) | O(u) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Partial removal of a value does not reduce unique count.
- `k` may remove all elements.
- Greedy uses smallest frequencies first.

## Related
- Top K Frequent Elements
- Sort Characters By Frequency
