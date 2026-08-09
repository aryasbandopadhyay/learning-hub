# 09. Maximum Erasure Value

- **Difficulty:** Medium
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google

## Problem
Given a list of positive integers `nums`, erase exactly one contiguous subarray containing only unique elements. Return the maximum possible sum of the erased subarray.

Implement `Solution.maximumUniqueSubarray` with the parameters below and return the requested value.

**Input**
- `nums`: a `list[int]`; the input integer list described above.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= len(nums) <= 100000`
- `1 <= nums[i] <= 10000`

## Examples
```text
Input: nums = [4, 2, 4, 5, 6]
Output: 17
Explanation: Erase `[2, 4, 5, 6]`, whose elements are unique and sum to 17.
```

## Understanding & Intuition
Positive values mean a longer unique window with more elements is always better until a duplicate appears. When a duplicate enters, advance the left edge until uniqueness is restored. Maintaining the window sum gives the score instantly.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate subarrays, rescan for duplicates, and sum valid ones.
```python
class Solution:
    def maximumUniqueSubarray(self, nums: list[int]) -> int:
        n = len(nums)
        ans = 0
        for i in range(n):
            for j in range(i, n):
                seen = set()
                total = 0
                ok = True
                for p in range(i, j + 1):
                    if nums[p] in seen:
                        ok = False
                    seen.add(nums[p])
                    total += nums[p]
                if ok:
                    ans = max(ans, total)
        return ans
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** For each start, extend until the first duplicate and update a running sum.
```python
class Solution:
    def maximumUniqueSubarray(self, nums: list[int]) -> int:
        n = len(nums)
        ans = 0
        for i in range(n):
            seen = set()
            total = 0
            for j in range(i, n):
                if nums[j] in seen:
                    break
                seen.add(nums[j])
                total += nums[j]
                ans = max(ans, total)
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Keep a unique window with a set and subtract values as the left edge advances.
```python
class Solution:
    def maximumUniqueSubarray(self, nums: list[int]) -> int:
        seen = set()
        left = 0
        total = 0
        ans = 0
        for x in nums:
            while x in seen:
                seen.remove(nums[left])
                total -= nums[left]
                left += 1
            seen.add(x)
            total += x
            ans = max(ans, total)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Values are positive; this is why maximizing the current unique window sum works.
- Remove from the left until the duplicate value is gone.

## Related
- Longest Substring Without Repeating Characters
- Subarray Product Less Than K
