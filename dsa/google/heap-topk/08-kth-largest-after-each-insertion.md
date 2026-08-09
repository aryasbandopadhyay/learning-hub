# 08. Kth Largest After Each Insertion

- **Difficulty:** Medium
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
Given a list `nums` representing stream insertions and an integer `k`, return a list where each position contains the kth largest value after that insertion. If fewer than `k` values have been seen, return `-1` at that position.

Implement `Solution.kthLargestAfterEachInsertion` with the parameters below and return the requested value.

**Input**
- `nums`: a `list[int]`; the input integer list described above.
- `k`: a `int`; the required count, window limit, or operation budget described above.

**Output**
- A list in insertion order where each value is the current kth largest value, or `-1` before at least `k` values have been seen.

## Constraints
- `1 <= len(nums) <= 100000`, `1 <= k <= len(nums)`

## Examples
```text
Input: nums = [4,5,8,2,10,9], k = 3
Output: [-1,-1,4,4,5,8]
Explanation: After each prefix of length at least 3, report the third largest prefix value. The result is shown in the required order.
```

## Understanding & Intuition
The kth largest is the smallest value among the top `k` values seen so far. Keeping exactly those top `k` values is enough. A min-heap of size `k` exposes the answer at its root.

## Approach 1 — Naive / Brute Force
**Idea:** Sort every prefix descending and take index `k - 1` when it exists.
```python
class Solution:
    def kthLargestAfterEachInsertion(self, nums: list[int], k: int) -> list[int]:
        ans = []
        for i in range(len(nums)):
            if i + 1 < k:
                ans.append(-1)
            else:
                ans.append(sorted(nums[:i + 1], reverse=True)[k - 1])
        return ans
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Maintain all seen numbers in sorted order and index the kth largest directly.
```python
class Solution:
    def kthLargestAfterEachInsertion(self, nums: list[int], k: int) -> list[int]:
        import bisect
        seen = []
        ans = []
        for x in nums:
            bisect.insort(seen, x)
            if len(seen) < k:
                ans.append(-1)
            else:
                ans.append(seen[-k])
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain a min-heap containing only the largest `k` values seen so far.
```python
class Solution:
    def kthLargestAfterEachInsertion(self, nums: list[int], k: int) -> list[int]:
        import heapq
        heap = []
        ans = []
        for x in nums:
            if len(heap) < k:
                heapq.heappush(heap, x)
            elif x > heap[0]:
                heapq.heapreplace(heap, x)
            ans.append(heap[0] if len(heap) == k else -1)
        return ans
```
- **Time:** O(n log k) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log k) | O(k) |

## Edge Cases & Pitfalls
- Duplicates count as separate stream elements.
- Use `-1` only before the kth value exists.
- Do not keep all values in the optimal heap.

## Related
- Kth Largest Element in an Array
- Running Median of a Stream
