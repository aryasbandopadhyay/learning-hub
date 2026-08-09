# 24. Range Sum Query - Immutable

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Design an immutable array wrapper `NumArray` that can answer range-sum queries. After construction, `sumRange(left, right)` returns the sum of `nums[left]` through `nums[right]`, inclusive.

**Input**
- `NumArray(nums)`: initializes the object with an integer list `nums`.
- `sumRange(left, right)`: asks for the inclusive sum between indices `left` and `right`.

**Output**
- For each `sumRange` call, return the requested integer sum. Constructor calls return `null`. **This judge compares exactly** to the operation output sequence.

## Constraints
- `1 <= nums.length <= 10^4`
- `-10^5 <= nums[i] <= 10^5`
- `0 <= left <= right < nums.length`
- At most `10^4` calls are made to `sumRange`.

## Examples
```text
Input: nums = [-2,0,3,-5,2,-1]; sumRange(0,2), sumRange(2,5), sumRange(0,5)
Output: [1,-1,-3]
Explanation: The sums are `-2+0+3 = 1`, `3+(-5)+2+(-1) = -1`, and the whole array sum is `-3`.
```

## Understanding & Intuition
Immutable repeated queries reward preprocessing. Prefix sums store the sum before each index, so inclusive ranges are one subtraction.

## Approach 1 — Naive / Brute Force
**Idea:** Store nums and sum each query.
```python
class NumArray:
    def __init__(self, nums: list[int]):
        self.nums = nums

    def sumRange(self, left: int, right: int) -> int:
        total = 0
        for i in range(left, right + 1):
            total += self.nums[i]
        return total
```
- **Time:** O(n) per query — **Space:** O(1) extra

## Approach 2 — Better
**Idea:** Precompute every possible range sum.
```python
class NumArray:
    def __init__(self, nums: list[int]):
        self.sums = {}
        for l in range(len(nums)):
            total = 0
            for r in range(l, len(nums)):
                total += nums[r]
                self.sums[(l, r)] = total

    def sumRange(self, left: int, right: int) -> int:
        return self.sums[(left, right)]
```
- **Time:** O(1) query, O(n^2) build — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Build prefix sums of length n+1.
```python
class NumArray:
    def __init__(self, nums: list[int]):
        self.prefix = [0]
        for x in nums:
            self.prefix.append(self.prefix[-1] + x)

    def sumRange(self, left: int, right: int) -> int:
        return self.prefix[right + 1] - self.prefix[left]
```
- **Time:** O(1) query, O(n) build — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) per query | O(1) extra |
| Better | O(1) query, O(n^2) build | O(n^2) |
| Optimal | O(1) query, O(n) build | O(n) |

## Edge Cases & Pitfalls
- Ranges are inclusive.
- Prefix length is n+1.
- No updates are needed.

## Related
- Range Sum Query 2D - Immutable
- Subarray Sum Equals K
