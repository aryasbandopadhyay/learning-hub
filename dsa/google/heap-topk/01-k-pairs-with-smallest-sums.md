# 01. K Pairs with Smallest Sums

- **Difficulty:** Medium
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
Given two sorted integer lists `nums1` and `nums2`, return the `k` pairs `[nums1[i], nums2[j]]` with the smallest sums. If sums tie, order pairs by first value, then second value, then their indices.

Implement `Solution.kSmallestPairs` with the parameters below and return the requested value.

**Input**
- `nums1`: a `list[int]`; the first integer list described above.
- `nums2`: a `list[int]`; the second integer list described above.
- `k`: a `int`; the required count, window limit, or operation budget described above.

**Output**
- A list of pairs. **This judge compares exactly**, so return pairs in ascending order by `(sum, nums1 value, nums2 value, index in nums1, index in nums2)` and stop after `k` pairs or after all pairs are used.

## Constraints
- `1 <= len(nums1), len(nums2) <= 2000`, `1 <= k <= 10000`, and values may be negative

## Examples
```text
Input: nums1 = [1,7,11], nums2 = [2,4,6], k = 3
Output: [[1,2],[1,4],[1,6]]
Explanation: The three smallest pair sums all use nums1[0]. The result is shown in the required order.
```

## Understanding & Intuition
The Cartesian product can be huge, but sorted inputs make each row of pairs sorted by sum. A min-heap can expand only the next unseen pair from each row. Tie-breaking keeps the output canonical.

## Approach 1 — Naive / Brute Force
**Idea:** Generate every pair, sort by the canonical key, and take the first `k`.
```python
class Solution:
    def kSmallestPairs(self, nums1: list[int], nums2: list[int], k: int) -> list[list[int]]:
        pairs = []
        for i, a in enumerate(nums1):
            for j, b in enumerate(nums2):
                pairs.append((a + b, a, b, i, j))
        pairs.sort()
        return [[a, b] for _, a, b, _, _ in pairs[:k]]
```
- **Time:** O(mn log(mn)) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Keep only the best `k` candidates seen so far in a max-heap, then sort those candidates canonically.
```python
class Solution:
    def kSmallestPairs(self, nums1: list[int], nums2: list[int], k: int) -> list[list[int]]:
        import heapq
        heap = []
        for i, a in enumerate(nums1):
            for j, b in enumerate(nums2):
                key = (a + b, a, b, i, j)
                item = tuple(-x for x in key)
                if len(heap) < k:
                    heapq.heappush(heap, item)
                elif item > heap[0]:
                    heapq.heapreplace(heap, item)
        best = [tuple(-x for x in item) for item in heap]
        best.sort()
        return [[a, b] for _, a, b, _, _ in best]
```
- **Time:** O(mn log k + k log k) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Seed the heap with the first pair from each useful row and expand a row only when its current pair is chosen.
```python
class Solution:
    def kSmallestPairs(self, nums1: list[int], nums2: list[int], k: int) -> list[list[int]]:
        import heapq
        heap = []
        for i in range(min(len(nums1), k)):
            heapq.heappush(heap, (nums1[i] + nums2[0], nums1[i], nums2[0], i, 0))
        ans = []
        while heap and len(ans) < k:
            _, a, b, i, j = heapq.heappop(heap)
            ans.append([a, b])
            if j + 1 < len(nums2):
                nj = j + 1
                heapq.heappush(heap, (nums1[i] + nums2[nj], nums1[i], nums2[nj], i, nj))
        return ans
```
- **Time:** O(k log min(k, m)) — **Space:** O(min(k, m))

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn log(mn)) | O(mn) |
| Better | O(mn log k + k log k) | O(k) |
| Optimal | O(k log min(k, m)) | O(min(k, m)) |

## Edge Cases & Pitfalls
- Return fewer than `k` pairs if the product has fewer pairs.
- Include indices in tie-breaking so duplicates stay deterministic.
- The heap expansion relies on both input lists being sorted.

## Related
- Kth Smallest Sum in a Sorted Matrix
- Merge K Sorted Lists
