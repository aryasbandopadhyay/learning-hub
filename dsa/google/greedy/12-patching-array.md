# 12. Patching Array

- **Difficulty:** Hard
- **Pattern:** greedy
- **Asked at:** Google

## Problem
Given a sorted positive integer array `nums` and an integer `n`, add the fewest positive integers so every number in `[1, n]` can be formed as a sum of some elements from the resulting array. Return the minimum number of added integers.

Constraints: `1 <= n <= 2^31 - 1`, `0 <= len(nums) <= 1000`, `1 <= nums[i] <= n`, and `nums` is sorted ascending.

## Examples
```text
Input: nums = [1,5,10], n = 20
Output: 2
Explanation: Patching 2 and 4 lets the array form every value from 1 through 20.
```

## Understanding & Intuition
Suppose all values in `[1, miss)` are currently formable. If the next array value is at most `miss`, adding it extends coverage to `[1, miss + value)`. Otherwise, `miss` itself cannot be formed, and patching exactly `miss` gives the largest possible coverage jump.

## Approach 1 — Naive / Brute Force
**Idea:** Track all reachable sums up to `n`; whenever the smallest missing sum cannot be formed, patch it.
```python
class Solution:
    def minPatches(self, nums: list[int], n: int) -> int:
        reachable = {0}
        patches = i = 0
        nums = list(nums)
        while True:
            miss = 1
            while miss <= n and miss in reachable:
                miss += 1
            if miss > n:
                return patches
            if i < len(nums) and nums[i] <= miss:
                x = nums[i]
                i += 1
            else:
                x = miss
                patches += 1
            for s in list(reachable):
                if s + x <= n:
                    reachable.add(s + x)
```
- **Time:** O(n * (m+p)) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Maintain the contiguous covered prefix `[1, miss)` but physically insert each patch into a working stream.
```python
class Solution:
    def minPatches(self, nums, n):
        arr = list(nums)
        patches = i = 0
        miss = 1
        while miss <= n:
            if i < len(arr) and arr[i] <= miss:
                miss += arr[i]
                i += 1
            else:
                arr.insert(i, miss)
                patches += 1
        return patches
```
- **Time:** O(m + p^2) — **Space:** O(m + p)

## Approach 3 — Optimal
**Idea:** Use the coverage invariant directly; consume usable nums, otherwise patch `miss` and double coverage.
```python
class Solution:
    def minPatches(self, nums, n):
        patches = i = 0
        miss = 1
        while miss <= n:
            if i < len(nums) and nums[i] <= miss:
                miss += nums[i]
                i += 1
            else:
                miss += miss
                patches += 1
        return patches
```
- **Time:** O(m + log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * (m+p)) | O(n) |
| Better | O(m + p^2) | O(m + p) |
| Optimal | O(m + log n) | O(1) |

## Edge Cases & Pitfalls
- Patch `miss`, not `miss - 1` or the next array number.
- Values larger than `miss` cannot help form `miss` yet.
- Use integer arithmetic; coverage can exceed `n` safely.

## Related
- Minimum Number of Taps to Open to Water a Garden
- Greedy Coverage
