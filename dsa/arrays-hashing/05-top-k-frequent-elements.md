# 05. Top K Frequent Elements

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Return the `k` most frequent elements of `nums` in any order. Constraints: `1 <= k <= unique count`, `n <= 10^5`.

## Examples
```text
Input: nums = [1,1,1,2,2,3], k = 2
Output: [1,2]
Explanation: 1 and 2 have the highest frequencies.
```

## Understanding & Intuition
After counting frequencies, the task is selecting largest counts. Sorting is easy; bucket sort is linear because frequency ranges from 1 to n.

## Approach 1 — Naive / Brute Force
**Idea:** Count each unique value by rescanning.
```python
class Solution:
    def topKFrequent(self, nums: list[int], k: int) -> list[int]:
        unique = []
        for x in nums:
            if x not in unique:
                unique.append(x)
        freq = []
        for x in unique:
            freq.append((sum(1 for y in nums if y == x), x))
        freq.sort(reverse=True)
        return [x for _, x in freq[:k]]
```
- **Time:** O(u*n + u log u) — **Space:** O(u)

## Approach 2 — Better
**Idea:** Count once and sort unique values by count.
```python
class Solution:
    def topKFrequent(self, nums: list[int], k: int) -> list[int]:
        counts = {}
        for x in nums:
            counts[x] = counts.get(x, 0) + 1
        items = sorted(counts, key=lambda x: counts[x], reverse=True)
        return items[:k]
```
- **Time:** O(n + u log u) — **Space:** O(u)

## Approach 3 — Optimal
**Idea:** Bucket values by frequency and read from high to low.
```python
class Solution:
    def topKFrequent(self, nums: list[int], k: int) -> list[int]:
        counts = {}
        for x in nums:
            counts[x] = counts.get(x, 0) + 1
        buckets = [[] for _ in range(len(nums) + 1)]
        for x, c in counts.items():
            buckets[c].append(x)
        ans = []
        for c in range(len(buckets) - 1, 0, -1):
            for x in buckets[c]:
                ans.append(x)
                if len(ans) == k:
                    return ans
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(u*n + u log u) | O(u) |
| Better | O(n + u log u) | O(u) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Any order is accepted.
- Negative numbers work as keys.
- Stop once k elements are collected.

## Related
- Kth Largest Element in an Array
- Sort Characters By Frequency
