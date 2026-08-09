# 03. Maximum Sum Circular Subarray

- **Difficulty:** Medium
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Amazon, Microsoft

## Problem
You are given a circular integer array `nums`, where the next element after the last index is the first index.

Choose a non-empty contiguous subarray. It may wrap around the end of the array, but it cannot use any element more than once. Return the maximum possible sum.

**Input**
- `nums`: a list of integers arranged in circular order.

**Output**
- The maximum sum of a non-empty circular contiguous subarray.

## Constraints
- `1 <= nums.length <= 3 * 10^4`
- `-3 * 10^4 <= nums[i] <= 3 * 10^4`

## Examples
```text
Input: nums = [5,-3,5]
Output: 10
Explanation: The best subarray wraps around, taking the last `5` and the first `5` for a total of `10`.
```

## Understanding & Intuition
The best answer is either a normal maximum subarray or a wrapping subarray. A wrapping subarray equals total sum minus the minimum middle subarray excluded. When all numbers are negative, the wrap formula would incorrectly choose an empty subarray.

## Approach 1 — Naive / Brute Force
**Idea:** Duplicate the array and try all circular starts with lengths up to `n`.
```python
class Solution:
    def maxSubarraySumCircular(self, nums: list[int]) -> int:
        n = len(nums)
        arr = nums + nums
        best = nums[0]
        for i in range(n):
            total = 0
            for length in range(1, n + 1):
                total += arr[i + length - 1]
                best = max(best, total)
        return best
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use prefix sums over the doubled array and check every valid circular interval by subtraction.
```python
class Solution:
    def maxSubarraySumCircular(self, nums: list[int]) -> int:
        n = len(nums)
        arr = nums + nums
        prefix = [0]
        for x in arr:
            prefix.append(prefix[-1] + x)
        best = nums[0]
        for i in range(n):
            for j in range(i + 1, i + n + 1):
                best = max(best, prefix[j] - prefix[i])
        return best
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Run Kadane for maximum and minimum subarray; compare normal max with `total - min_subarray` unless all values are negative.
```python
class Solution:
    def maxSubarraySumCircular(self, nums: list[int]) -> int:
        total = sum(nums)
        cur_max = best_max = nums[0]
        cur_min = best_min = nums[0]
        for x in nums[1:]:
            cur_max = max(x, cur_max + x)
            best_max = max(best_max, cur_max)
            cur_min = min(x, cur_min + x)
            best_min = min(best_min, cur_min)
        if best_max < 0:
            return best_max
        return max(best_max, total - best_min)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- All-negative arrays must return the largest element.
- The wrapping answer excludes one non-empty middle segment.
- Do not allow a circular subarray to take more than `n` elements.

## Related
- Maximum Subarray
- Maximum Product Subarray
