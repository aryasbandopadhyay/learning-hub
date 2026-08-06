# 07. Reduce Array Size to The Half

- **Difficulty:** Medium
- **Pattern:** greedy
- **Asked at:** Google

## Problem
Given an integer array `arr`, choose a set of distinct integer values and remove every occurrence of those values. Return the minimum size of such a set so that at least half of the array elements are removed.

Constraints: `1 <= len(arr) <= 10^5`, `len(arr)` is even, `1 <= arr[i] <= 10^5`.

## Examples
```text
Input: arr = [3,3,3,3,5,5,5,2,2,7]
Output: 2
Explanation: Removing values 3 and 5 removes 7 elements, at least half of the array.
```

## Understanding & Intuition
The value identities do not matter after counting frequencies. To remove many elements using few distinct values, always pick the largest remaining frequency. This greedy choice cannot hurt because any smaller chosen frequency could be swapped for a larger one.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly scan all remaining frequencies to pick the largest one.
```python
class Solution:
    def minSetSize(self, arr: list[int]) -> int:
        from collections import Counter
        freqs = list(Counter(arr).values())
        removed = ans = 0
        target = len(arr) // 2
        while removed < target:
            best = max(range(len(freqs)), key=lambda i: freqs[i])
            removed += freqs[best]
            freqs[best] = 0
            ans += 1
        return ans
```
- **Time:** O(k^2) — **Space:** O(k)

## Approach 2 — Better
**Idea:** Sort frequencies descending and take them until enough elements are removed.
```python
class Solution:
    def minSetSize(self, arr):
        from collections import Counter
        removed = 0
        target = len(arr) // 2
        for ans, f in enumerate(sorted(Counter(arr).values(), reverse=True), 1):
            removed += f
            if removed >= target:
                return ans
        return 0
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Frequency values range from `1` to `n`, so bucket-count frequencies and consume largest buckets first.
```python
class Solution:
    def minSetSize(self, arr):
        from collections import Counter
        buckets = [0] * (len(arr) + 1)
        for f in Counter(arr).values():
            buckets[f] += 1
        removed = ans = 0
        target = len(arr) // 2
        for f in range(len(arr), 0, -1):
            while buckets[f] and removed < target:
                removed += f
                ans += 1
                buckets[f] -= 1
            if removed >= target:
                return ans
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(k^2) | O(k) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- The target is at least half, so crossing the threshold is enough.
- Count distinct values, not removed elements.
- Sorting values themselves instead of frequencies is unnecessary.

## Related
- Top K Frequent Elements
- Greedy Frequency Selection
