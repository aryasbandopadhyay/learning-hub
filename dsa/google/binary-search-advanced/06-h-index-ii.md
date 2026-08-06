# 06. H-Index II

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
Given a sorted nondecreasing list `citations`, where `citations[i]` is the citation count of the `i`th paper, return the researcher's h-index. The h-index is the maximum `h` such that at least `h` papers have at least `h` citations each.

Constraints: `1 <= len(citations) <= 10^5`, `0 <= citations[i] <= 10^6`.

## Examples
```text
Input: citations = [0,1,3,5,6]
Output: 3
Explanation: Three papers have at least 3 citations, but only two have at least 4.
```

## Understanding & Intuition
At index `i`, there are `n - i` papers with at least `citations[i]` citations. We need the leftmost index where `citations[i] >= n - i`.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible h-index and keep the largest valid value.
```python
class Solution:
    def hIndex(self, citations: list[int]) -> int:
        n = len(citations)
        ans = 0
        for h in range(n + 1):
            count = 0
            for c in citations:
                if c >= h:
                    count += 1
            if count >= h:
                ans = h
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Scan from the first paper to find the first valid threshold.
```python
class Solution:
    def hIndex(self, citations: list[int]) -> int:
        n = len(citations)
        for i, c in enumerate(citations):
            h = n - i
            if c >= h:
                return h
        return 0
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search the first index satisfying `citations[i] >= n - i`.
```python
class Solution:
    def hIndex(self, citations: list[int]) -> int:
        n = len(citations)
        lo, hi = 0, n
        while lo < hi:
            mid = (lo + hi) // 2
            if citations[mid] >= n - mid:
                hi = mid
            else:
                lo = mid + 1
        return n - lo
```
- **Time:** O(log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- All zeros should return 0.
- The h-index cannot exceed the number of papers.
- The input is already sorted; do not sort and lose the intended pattern.

## Related
- Binary Search
- Search Insert Position
