# 07. Smallest Divisor Given a Threshold

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
Given positive integers `nums` and an integer `threshold`, choose a positive integer divisor. For each number, divide by the divisor and round up, then sum the results. Return the smallest divisor whose sum is at most `threshold`.

Constraints: `1 <= len(nums) <= 5*10^4`, `1 <= nums[i] <= 10^6`, `len(nums) <= threshold <= 10^6`.

## Examples
```text
Input: nums = [1,2,5,9], threshold = 6
Output: 5
Explanation: Divisor 5 gives ceil values [1,1,1,2] with sum 5, while divisor 4 gives sum 7.
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
