# 18. Height Checker

- **Difficulty:** Easy
- **Pattern:** Sorting / Counting
- **Asked at:** Salesforce, Amazon, Google

## Problem
Return how many indices differ between `heights` and its nondecreasing sorted order.

## Examples
```text
Input: heights = [1,1,4,2,1,3]
Output: 3
Explanation: Expected is [1,1,1,2,3,4], with three mismatches.
```

## Understanding & Intuition
Compare current order to sorted order. Since heights are bounded, counting sort can produce expected values in linear time.

## Approach 1 — Naive / Brute Force
**Idea:** Selection-sort a copy, then compare.
```python
class Solution:
    def heightChecker(self, heights: list[int]) -> int:
        expected = heights[:]
        for i in range(len(expected)):
            m = min(range(i, len(expected)), key=lambda j: expected[j])
            expected[i], expected[m] = expected[m], expected[i]
        return sum(a != b for a, b in zip(heights, expected))
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort with the built-in sort and compare.
```python
class Solution:
    def heightChecker(self, heights: list[int]) -> int:
        return sum(a != b for a, b in zip(heights, sorted(heights)))
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Count heights and stream the expected sorted values.
```python
class Solution:
    def heightChecker(self, heights: list[int]) -> int:
        counts = [0] * 101
        for h in heights: counts[h] += 1
        expected = 1; ans = 0
        for h in heights:
            while counts[expected] == 0: expected += 1
            if h != expected: ans += 1
            counts[expected] -= 1
        return ans
```
- **Time:** O(n + k) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n + k) | O(k) |

## Edge Cases & Pitfalls
- Count mismatched positions, not swaps.
- Duplicates matter.
- Counting sort relies on bounded heights.

## Related
- Relative Sort Array
- Sort Array By Parity
