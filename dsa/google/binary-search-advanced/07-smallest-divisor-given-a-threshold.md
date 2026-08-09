# 07. Smallest Divisor Given a Threshold

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
You are given an integer array `nums` and an integer `threshold`.

Choose a positive divisor. Divide each number by it, round each quotient up to the nearest integer, and sum those rounded values. Return the smallest divisor whose sum is at most `threshold`.

**Input**
- `nums`: a list of positive integers.
- `threshold`: the maximum allowed rounded-sum value.

**Output**
- The smallest positive divisor satisfying the threshold.

## Constraints
- `1 <= nums.length <= 5 * 10^4`
- `1 <= nums[i] <= 10^6`
- `nums.length <= threshold <= 10^6`

## Examples
```text
Input: nums = [1,2,5,9], threshold = 6
Output: 5
Explanation: With divisor `5`, the rounded quotients are `1,1,1,2`, summing to `5`, which is at most `6`; smaller divisors do not work.
```

## Understanding & Intuition
As the divisor increases, every rounded quotient stays the same or decreases. This monotonic relationship lets us binary search the smallest feasible divisor.

## Approach 1 — Naive / Brute Force
**Idea:** Test divisors from 1 upward until the rounded sum fits.
```python
class Solution:
    def smallestDivisor(self, nums: list[int], threshold: int) -> int:
        for d in range(1, max(nums) + 1):
            total = 0
            for x in nums:
                total += (x + d - 1) // d
            if total <= threshold:
                return d
        return max(nums)
```
- **Time:** O(nM) — **Space:** O(1), where `M = max(nums)`

## Approach 2 — Better
**Idea:** Binary search the value range using a helper function for feasibility.
```python
class Solution:
    def smallestDivisor(self, nums: list[int], threshold: int) -> int:
        def ok(d: int) -> bool:
            return sum((x + d - 1) // d for x in nums) <= threshold
        lo, hi = 1, max(nums)
        while lo < hi:
            mid = (lo + hi) // 2
            if ok(mid):
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(n log M) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use the same binary search but stop feasibility counting early once the threshold is exceeded.
```python
class Solution:
    def smallestDivisor(self, nums: list[int], threshold: int) -> int:
        lo, hi = 1, max(nums)
        while lo < hi:
            mid = (lo + hi) // 2
            total = 0
            for x in nums:
                total += (x + mid - 1) // mid
                if total > threshold:
                    break
            if total <= threshold:
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(n log M) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nM) | O(1) |
| Better | O(n log M) | O(1) |
| Optimal | O(n log M) | O(1) |

## Edge Cases & Pitfalls
- Use integer ceiling formula, not floating point division.
- The answer is at most `max(nums)` because threshold is at least `len(nums)`.
- Search for the smallest feasible divisor, so move `hi` when feasible.

## Related
- Koko Eating Bananas
- Capacity to Ship Packages Within D Days
