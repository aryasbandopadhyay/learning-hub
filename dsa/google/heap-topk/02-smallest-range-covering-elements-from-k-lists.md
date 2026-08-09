# 02. Smallest Range Covering Elements from K Lists

- **Difficulty:** Hard
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
Given `nums`, a list of `k` sorted non-empty integer lists, return the smallest inclusive range `[left, right]` that contains at least one number from each list. If ranges have the same length, return the one with the smaller `left`.

Implement `Solution.smallestRange` with the parameters below and return the requested value.

**Input**
- `nums`: a `list[list[int]]`; the input integer list described above.

**Output**
- A two-element list `[left, right]` for the chosen inclusive range. If multiple ranges have the same width, return the one with the smaller `left` endpoint.

## Constraints
- `1 <= k <= 3500`, total elements `<= 3500`

## Examples
```text
Input: nums = [[4,10,15,24,26],[0,9,12,20],[5,18,22,30]]
Output: [20,24]
Explanation: The range includes 24 from the first list, 20 from the second, and 22 from the third.
```

## Understanding & Intuition
A valid range is determined by a minimum chosen value and the current maximum among one element from each list. Moving the list that owns the minimum is the only move that can shrink the range. A heap gives that minimum while tracking the current maximum.

## Approach 1 — Naive / Brute Force
**Idea:** Try every value as a left endpoint and scan each list for its first value at least that endpoint.
```python
class Solution:
    def smallestRange(self, nums: list[list[int]]) -> list[int]:
        import bisect
        values = sorted(set(x for arr in nums for x in arr))
        best = [-10**18, 10**18]
        for left in values:
            right = left
            ok = True
            for arr in nums:
                i = bisect.bisect_left(arr, left)
                if i == len(arr):
                    ok = False
                    break
                right = max(right, arr[i])
            if ok and (right - left < best[1] - best[0] or (right - left == best[1] - best[0] and left < best[0])):
                best = [left, right]
        return best
```
- **Time:** O(NK log M) — **Space:** O(N)

## Approach 2 — Better
**Idea:** Sort all values with their list id and use a sliding window that contains every id.
```python
class Solution:
    def smallestRange(self, nums: list[list[int]]) -> list[int]:
        from collections import defaultdict
        merged = []
        for r, arr in enumerate(nums):
            for x in arr:
                merged.append((x, r))
        merged.sort()
        need = len(nums)
        counts = defaultdict(int)
        have = 0
        left = 0
        best = [-10**18, 10**18]
        for right, (x, r) in enumerate(merged):
            if counts[r] == 0:
                have += 1
            counts[r] += 1
            while have == need:
                lo = merged[left][0]
                if x - lo < best[1] - best[0] or (x - lo == best[1] - best[0] and lo < best[0]):
                    best = [lo, x]
                lr = merged[left][1]
                counts[lr] -= 1
                if counts[lr] == 0:
                    have -= 1
                left += 1
        return best
```
- **Time:** O(N log N) — **Space:** O(N)

## Approach 3 — Optimal
**Idea:** Keep one pointer per list in a min-heap and advance only the list currently providing the minimum.
```python
class Solution:
    def smallestRange(self, nums: list[list[int]]) -> list[int]:
        import heapq
        heap = []
        current_max = -10**18
        for r, arr in enumerate(nums):
            heapq.heappush(heap, (arr[0], r, 0))
            current_max = max(current_max, arr[0])
        best = [heap[0][0], current_max]
        while True:
            val, r, c = heapq.heappop(heap)
            if current_max - val < best[1] - best[0] or (current_max - val == best[1] - best[0] and val < best[0]):
                best = [val, current_max]
            if c + 1 == len(nums[r]):
                return best
            nxt = nums[r][c + 1]
            current_max = max(current_max, nxt)
            heapq.heappush(heap, (nxt, r, c + 1))
```
- **Time:** O(N log K) — **Space:** O(K)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(NK log M) | O(N) |
| Better | O(N log N) | O(N) |
| Optimal | O(N log K) | O(K) |

## Edge Cases & Pitfalls
- Lists are non-empty; otherwise no covering range exists.
- Use inclusive range length `right - left`.
- Tie-break by smaller left endpoint.

## Related
- Merge K Sorted Arrays
- Minimum Window Substring
