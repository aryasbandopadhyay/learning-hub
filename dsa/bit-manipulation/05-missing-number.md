# 05. Missing Number

- **Difficulty:** Easy
- **Pattern:** Bit Manipulation
- **Asked at:** Amazon, Microsoft, Google, Meta

## Problem
Given an array `nums` containing `n` distinct numbers in the range `[0, n]`, return the only number in the range that is missing from the array. Constraints: `1 <= n <= 10^4`, `0 <= nums[i] <= n`, and all numbers are unique.

## Examples
```text
Input: nums = [3,0,1]
Output: 2
Explanation: The values should be 0, 1, 2, 3, and 2 is missing.
```

## Understanding & Intuition
The full range is known, so we can compare the array against `0..n`. XOR cancels values that appear in both the expected range and the actual array. The survivor is the missing number.

## Approach 1 — Naive / Brute Force
**Idea:** For every value in `[0, n]`, scan the array to see whether it exists.
```python
from typing import List

class Solution:
    def missingNumber(self, nums: List[int]) -> int:
        n = len(nums)
        for target in range(n + 1):
            found = False
            for x in nums:
                if x == target:
                    found = True
                    break
            if not found:
                return target
        return -1
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use the arithmetic sum formula and subtract actual values.
```python
from typing import List

class Solution:
    def missingNumber(self, nums: List[int]) -> int:
        n = len(nums)
        expected = n * (n + 1) // 2
        actual = sum(nums)
        return expected - actual
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** XOR all indices and values, plus `n`, so paired values cancel.
```python
from typing import List

class Solution:
    def missingNumber(self, nums: List[int]) -> int:
        missing = len(nums)
        for i, x in enumerate(nums):
            # Index i represents an expected value; x is an actual value.
            missing ^= i ^ x
        return missing
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- The missing number may be `0` or `n`.
- The array has length `n`, but the valid range has `n + 1` values.
- Sum formula is simple in Python because integers do not overflow.

## Related
- Single Number
- Counting Bits
- Bitwise AND of Numbers Range
