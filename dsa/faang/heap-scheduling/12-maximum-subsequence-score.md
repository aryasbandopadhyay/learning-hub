# 12. Maximum Subsequence Score

- **Difficulty:** Medium
- **Pattern:** Heap / Greedy
- **Asked at:** Google, Amazon, Meta

## Problem
You are given two arrays `nums1` and `nums2` of equal length and an integer `k`. Choose exactly `k` indices. The score is the sum of chosen `nums1` values multiplied by the minimum chosen `nums2` value. Return the maximum possible score.

Constraints: `1 <= k <= len(nums1) <= 10^5`, `0 <= nums1[i], nums2[i] <= 10^5`.

## Examples
```text
Input: nums1 = [1,3,3,2], nums2 = [2,1,3,4], k = 3
Output: 12
Explanation: Choose indices 0, 2, and 3. The score is (1 + 3 + 2) * min(2, 3, 4) = 12.
```

## Understanding & Intuition
If a chosen index has the minimum `nums2` value, then all other chosen indices must have `nums2` at least that value. For a fixed minimum candidate, we want the largest possible sum of `k` corresponding `nums1` values. Sorting by `nums2` descending lets us consider each candidate minimum once.

## Approach 1 — Naive / Brute Force
**Idea:** Try every combination of `k` indices, compute its sum and minimum, and keep the best score.
```python
class Solution:
    def maxScore(self, nums1, nums2, k):
        from itertools import combinations
        n = len(nums1)
        best = 0
        for combo in combinations(range(n), k):
            total = 0
            minimum = None
            for idx in combo:
                total += nums1[idx]
                if minimum is None or nums2[idx] < minimum:
                    minimum = nums2[idx]
            best = max(best, total * minimum)
        return best
```
- **Time:** O(C(n,k) k) — **Space:** O(k)

## Approach 2 — Better
**Idea:** Treat each distinct position as the index supplying the minimum `nums2`. Collect all `nums1` values whose `nums2` is at least that threshold, sort them, and use the largest `k` values.
```python
class Solution:
    def maxScore(self, nums1, nums2, k):
        n = len(nums1)
        best = 0
        for i in range(n):
            threshold = nums2[i]
            candidates = []
            for j in range(n):
                if nums2[j] >= threshold:
                    candidates.append(nums1[j])
            if len(candidates) >= k:
                candidates.sort(reverse=True)
                best = max(best, sum(candidates[:k]) * threshold)
        return best
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort pairs by `nums2` descending. Maintain the largest `k` `nums1` values seen so far in a min-heap; when the heap has size `k`, the current `nums2` is the minimum for that candidate group.
```python
class Solution:
    def maxScore(self, nums1, nums2, k):
        import heapq
        pairs = sorted(zip(nums2, nums1), reverse=True)
        heap = []
        total = 0
        best = 0
        for value2, value1 in pairs:
            heapq.heappush(heap, value1)
            total += value1
            if len(heap) > k:
                total -= heapq.heappop(heap)
            if len(heap) == k:
                best = max(best, total * value2)
        return best
```
- **Time:** O(n log n) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(C(n,k) k) | O(k) |
| Better | O(n^2 log n) | O(n) |
| Optimal | O(n log n) | O(k) |

## Edge Cases & Pitfalls
- Exactly `k` indices must be chosen.
- Sorting by `nums2` descending makes the current `nums2` the minimum among selected candidates.
- Keep the heap sum synchronized when popping the smallest `nums1`.

## Related
- IPO
- Maximum Performance of a Team
- K Closest Points to Origin
